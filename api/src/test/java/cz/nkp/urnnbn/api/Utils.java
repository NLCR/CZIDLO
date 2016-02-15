package cz.nkp.urnnbn.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
}
