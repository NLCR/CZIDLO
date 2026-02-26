package cz.nkp.urnnbn.czidlo_web_api;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Filtr, který zajistí, že hlavička Last-Modified v odpovědi bude vždy ve správném formátu RFC 1123.
 * Pokud je hlavička přítomna, ale není ve správném formátu, je přepsána na aktuální čas ve formátu RFC 1123.
 * Pokud hlavička chybí, nedělá filtr nic.
 * Jersey někdy generuje Last-Modified v nesprávném formátu (např. 'čt, 11 pro 2025 13:41:27 SEČ'),
 * což způsobuje zahlcující log se stacktracem v catalina.out, když Tomcat takovou hlavičku detekuje a zahazuje.
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class LastModifiedSanitizingFilter implements ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LastModifiedSanitizingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // 1) najdi existující hlavičku(y) Last-Modified (case-insensitive)
        List<String> headerNames = new ArrayList<>();
        for (String name : responseContext.getHeaders().keySet()) {
            if ("last-modified".equalsIgnoreCase(name)) {
                headerNames.add(name);
            }
        }

        if (headerNames.isEmpty()) {
            // žádný Last-Modified → neděláme nic
            return;
        }

        // vezmeme první hodnotu (typicky tam bude jedna)
        String originalValue = null;
        String canonicalName = headerNames.get(0); // např. "Last-modified"
        Object first = responseContext.getHeaders().getFirst(canonicalName);
        if (first != null) {
            originalValue = first.toString();
        }

        if (originalValue == null) {
            // prázdná hodnota → prostě odstraníme
            headerNames.forEach(n -> responseContext.getHeaders().remove(n));
            return;
        }

        // 2) zkusíme naparsovat jako RFC 1123
        try {
            DateTimeFormatter rfc1123 = DateTimeFormatter.RFC_1123_DATE_TIME;
            rfc1123.parse(originalValue);
            // → už je to validní RFC 1123, necháme jak je
            return;
        } catch (DateTimeParseException ex) {
            // není to RFC 1123 → přepíšeme
        }

        // 3) odstraníme všechny varianty názvu hlavičky
        headerNames.forEach(n -> responseContext.getHeaders().remove(n));

        // 4) nastavíme novou hodnotu – RFC 1123, GMT, čisté ASCII
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String fixed = DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
        LOG.fine("Last-Modified header value '" + originalValue + "' is not RFC 1123 compliant, replaced with '" + fixed + "'");
        responseContext.getHeaders().add("Last-Modified", fixed);
    }
}
