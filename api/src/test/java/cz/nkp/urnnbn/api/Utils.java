package cz.nkp.urnnbn.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class Utils {

    public static String readXsd(String urlString) {
        InputStream in = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
            in = con.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static NamespaceContext buildNsContext(final String prefix, final String uri) {
        return new NamespaceContext() {

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                List<String> list = new ArrayList<String>();
                list.add(prefix);
                return list.iterator();
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return prefix;
            }

            @Override
            public String getNamespaceURI(String prefix) {
                return uri;
            }
        };
    }

    /**
     * Url-encodes only reserved characters from rfc3986.
     * 
     * @see https://www.ietf.org/rfc/rfc3986.txt
     * @param original
     * @return
     */
    public static String urlEncodeReservedCharacters(String original) {
        Map<Character, String> translations = new HashMap<>();
        translations.put('!', "%21");
        translations.put('#', "%23");
        translations.put('$', "%24");
        translations.put('&', "%26");
        translations.put('\'', "%27");
        translations.put('(', "%28");
        translations.put(')', "%29");
        translations.put('*', "%2A");
        translations.put('+', "%2B");
        translations.put(',', "%2C");
        translations.put('/', "%2F");
        translations.put(':', "%3A");
        translations.put(';', "%3B");
        translations.put('=', "%3D");
        translations.put('?', "%3F");
        translations.put('@', "%40");
        translations.put('[', "%5B");
        translations.put(']', "%5D");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char c = original.charAt(i);
            if (translations.containsKey(c)) {
                builder.append(translations.get(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * Url-encodes only reserved and unreserverd characters from rfc3986.
     * 
     * @see https://www.ietf.org/rfc/rfc3986.txt
     * @param original
     * @return
     */
    public static String urlEncodeReservedAndUnreserved(String original) {
        Map<Character, String> translations = new HashMap<>();
        // reserved
        translations.put('!', "%21");
        translations.put('#', "%23");
        translations.put('$', "%24");
        translations.put('&', "%26");
        translations.put('\'', "%27");
        translations.put('(', "%28");
        translations.put(')', "%29");
        translations.put('*', "%2A");
        translations.put('+', "%2B");
        translations.put(',', "%2C");
        translations.put('/', "%2F");
        translations.put(':', "%3A");
        translations.put(';', "%3B");
        translations.put('=', "%3D");
        translations.put('?', "%3F");
        translations.put('@', "%40");
        translations.put('[', "%5B");
        translations.put(']', "%5D");
        // unreserved
        translations.put('-', "%2D");
        translations.put('.', "%2E");
        translations.put('_', "%5f");
        translations.put('~', "%7E");
        translations.put('0', "%30");
        translations.put('1', "%31");
        translations.put('2', "%32");
        translations.put('3', "%33");
        translations.put('4', "%34");
        translations.put('5', "%35");
        translations.put('6', "%36");
        translations.put('7', "%37");
        translations.put('8', "%38");
        translations.put('9', "%39");
        translations.put('a', "%61");
        translations.put('b', "%62");
        translations.put('c', "%63");
        translations.put('d', "%64");
        translations.put('e', "%65");
        translations.put('f', "%66");
        translations.put('g', "%67");
        translations.put('h', "%68");
        translations.put('i', "%69");
        translations.put('j', "%6A");
        translations.put('k', "%6B");
        translations.put('l', "%6C");
        translations.put('m', "%6D");
        translations.put('n', "%6E");
        translations.put('o', "%6F");
        translations.put('p', "%70");
        translations.put('q', "%71");
        translations.put('r', "%72");
        translations.put('s', "%73");
        translations.put('t', "%74");
        translations.put('u', "%75");
        translations.put('v', "%76");
        translations.put('w', "%77");
        translations.put('x', "%78");
        translations.put('y', "%79");
        translations.put('z', "%7A");
        translations.put('A', "%41");
        translations.put('B', "%42");
        translations.put('C', "%43");
        translations.put('D', "%44");
        translations.put('E', "%45");
        translations.put('F', "%46");
        translations.put('G', "%47");
        translations.put('H', "%48");
        translations.put('I', "%49");
        translations.put('J', "%4A");
        translations.put('K', "%4B");
        translations.put('L', "%4C");
        translations.put('M', "%4D");
        translations.put('N', "%4E");
        translations.put('O', "%4F");
        translations.put('P', "%50");
        translations.put('Q', "%51");
        translations.put('R', "%52");
        translations.put('S', "%53");
        translations.put('T', "%54");
        translations.put('U', "%55");
        translations.put('V', "%56");
        translations.put('W', "%57");
        translations.put('X', "%58");
        translations.put('Y', "%59");
        translations.put('Z', "%5A");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char c = original.charAt(i);
            if (translations.containsKey(c)) {
                builder.append(translations.get(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

}
