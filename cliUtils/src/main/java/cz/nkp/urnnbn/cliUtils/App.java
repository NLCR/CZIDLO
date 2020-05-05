package cz.nkp.urnnbn.cliUtils;

import java.security.NoSuchAlgorithmException;

/**
 * Created by Martin Řehánek on 8.2.18.
 */
public class App {

    private static final String ACTION_BUILD_SOLR_PASSWORD_HASH = "build_solr_password_hash";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: not enough parameters");
            System.err.println(buildUsage());
            System.exit(1);
        } else {
            String action = args[0];
            switch (action) {
                case ACTION_BUILD_SOLR_PASSWORD_HASH:
                    buildSolrBasicAuthPluginCredentials(withoutFirstItem(args));
                    break;
                default:
                    System.err.println("unknown action '" + action + "'");
                    System.err.println(buildUsage());
            }
        }
    }

    private static void buildSolrBasicAuthPluginCredentials(String[] args) {
        if (args.length < 2) {
            System.err.println("Error: not enough parameters");
            System.err.println(buildUsage(ACTION_BUILD_SOLR_PASSWORD_HASH));
        } else if (args.length > 2) {
            System.err.println("Error: to many parameters");
            System.err.println(buildUsage(ACTION_BUILD_SOLR_PASSWORD_HASH));
        } else {
            String login = args[0];
            String password = args[1];
            try {
                System.out.println("\t\"credentials\": {");
                System.out.println("\t\t\"" + login + "\": \"" + Utils.buildHashOfSaltAndPasswordForSolr(password) + "\"");
                System.out.println("\t}");
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Error: unknown algorithm: " + e.getMessage());
            }
        }
    }

    private static String[] withoutFirstItem(String[] original) {
        String[] result = new String[original.length - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = original[i + 1];
        }
        return result;
    }

    private static String buildUsage() {
        StringBuilder builder = new StringBuilder();
        builder.append("Usage: java -jar cliUtils.jar ACTION").append('\n');
        builder.append('\t').append("Available actions: ");
        builder.append(ACTION_BUILD_SOLR_PASSWORD_HASH);
        builder.append('\n');
        return builder.toString();
    }


    private static String buildUsage(String action) {
        StringBuilder builder = new StringBuilder();
        builder.append("Usage: java -jar cliUtils.jar ").append(action);
        switch (action) {
            case ACTION_BUILD_SOLR_PASSWORD_HASH:
                builder.append(" LOGIN PASSWORD").append('\n');
                builder.append('\t').append("Result of this action is credentials containing login, Base64-encoded hash of password+salt and Base64-encoded salt. " +
                        "This should be put in security.json in authentication section for BasicAuthPlugin. For example:").append('\n');
                builder.append("\"credentials\": {").append('\n');
                builder.append('\t').append("\"czidloIndexer\": \"IV0EHq1OnNrj6gvRCwvFwTrZ1+z1oBbnQdiVC3otuq0= Ndd7LKvVBAaZIF0QAVi1ekCfAJXr1GGfLtRUXhgrF8c=\"").append('\n');
                builder.append("}").append('\n');
                break;
        }
        return builder.toString();
    }

}
