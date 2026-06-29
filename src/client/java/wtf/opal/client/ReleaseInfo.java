package wtf.opal.client;

public final class ReleaseInfo {

    public static final ReleaseChannel CHANNEL = ReleaseChannel.DEVELOPMENT;
    public static final String VERSION = "0.1-beta.1";
    public static final String NAME = "Opal";

    public enum ReleaseChannel {
        PUBLIC("public"),
        BETA("beta"),
        DEVELOPMENT("development");

        private final String name;

        ReleaseChannel(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
