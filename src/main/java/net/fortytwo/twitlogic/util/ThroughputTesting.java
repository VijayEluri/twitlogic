package net.fortytwo.twitlogic.util;

import net.fortytwo.twitlogic.TwitLogic;
import net.fortytwo.twitlogic.flow.Handler;
import net.fortytwo.twitlogic.flow.NullHandler;
import net.fortytwo.twitlogic.model.Tweet;
import net.fortytwo.twitlogic.model.TweetParseException;
import net.fortytwo.twitlogic.persistence.SailFactory;
import net.fortytwo.twitlogic.persistence.TweetPersister;
import net.fortytwo.twitlogic.persistence.TweetStore;
import net.fortytwo.twitlogic.persistence.sail.NewAllegroSailFactory;
import net.fortytwo.twitlogic.services.twitter.TweetHandlerException;
import net.fortytwo.twitlogic.services.twitter.TwitterAPI;
import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * User: josh
 * Date: Aug 5, 2010
 * Time: 3:10:06 PM
 */
public class ThroughputTesting {
    private static final Random RANDOM = new Random();
    private static final Date REFERENCE_DATE = new Date();

    public static void main(final String[] args) throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("/tmp/twitlogic-throughput-testing.properties"));
        TwitLogic.setConfiguration(props);

        //testNullHandler();
        //testMemoryPersister();
        //testNativeStorePersister();
        testAllegroGraphPersister();

/*
        //System.out.println("" + Integer.MAX_VALUE);
        for (int i = 0; i < 10; i++) {
//            System.out.println(randomDateString());
//            System.out.println(randomString(70, 140));
//            System.out.println(randomUrlString(10, 50));
//            System.out.println(randomUrl());
//            System.out.println(randomTweet());
        }
        //*/
    }

    ////////////////////////////////////////////////////////////////////////////

    // Around 14,000 t/s on the reference machine.

    private static void testNullHandler() throws Exception {
        Handler<Tweet, TweetHandlerException> h = new NullHandler<Tweet, TweetHandlerException>();
        stressTest(h, 10000);
    }

    // Quickly reaches a peak of around 1,900 t/s before slowing down and failing due to lack of memory.
    private static void testMemoryPersister() throws Exception {
        Sail sail = new MemoryStore();
        sail.initialize();

        try {
            TweetStore store = new TweetStore(sail);
            store.initialize();
            try {
                TweetPersister p = new TweetPersister(store, null);

                stressTest(p, 1000);
            } finally {
                store.shutDown();
            }
        } finally {
            sail.shutDown();
        }
    }

    // Around 300 t/s for a small store.
    private static void testNativeStorePersister() throws Exception {
        File dir = new File("/tmp/twitlogic-stresstest-ns");
        if (dir.exists()) {
            deleteDirectory(dir);
        }

        Sail sail = new NativeStore(dir);
        sail.initialize();

        try {
            TweetStore store = new TweetStore(sail);
            store.initialize();
            try {
                TweetPersister p = new TweetPersister(store, null);

                stressTest(p, 1000);
            } finally {
                store.shutDown();
            }
        } finally {
            sail.shutDown();
        }
    }

    // Over the LAN: 6 t/s
    private static void testAllegroGraphPersister() throws Exception {
        SailFactory f = new NewAllegroSailFactory(TwitLogic.getConfiguration(), false);
        Sail sail = f.makeSail();
        sail.initialize();

        try {
            TweetStore store = new TweetStore(sail);
            store.initialize();
            try {
                TweetPersister p = new TweetPersister(store, null);

                stressTest(p, 1000);
            } finally {
                store.shutDown();
            }
        } finally {
            sail.shutDown();
        }
    }

    private static boolean deleteDirectory(final File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return (dir.delete());
    }

    ////////////////////////////////////////////////////////////////////////////

    private static int randomInteger(final int min,
                                     final int max) {
        return min + RANDOM.nextInt(1 + max - min);
    }

    private static String randomString(final int minLength,
                                       final int maxLength) {
        int l = randomInteger(minLength, maxLength);
        byte[] b = new byte[l];
        for (int i = 0; i < l; i++) {
            b[i] = (byte) (35 + RANDOM.nextInt(56));
        }
        return new String(b);
    }

    private static String randomUrlString(final int minLength,
                                          final int maxLength) {
        int l = randomInteger(minLength, maxLength);
        byte[] b = new byte[l];
        for (int i = 0; i < l; i++) {
            b[i] = (byte) (47 + RANDOM.nextInt(11));
        }
        return new String(b);
    }

    private static String randomUrl() {
        return "http://example.org/" + randomUrlString(10, 100);
    }

    private static String randomDateString() {
        Date d = new Date(REFERENCE_DATE.getTime() - RANDOM.nextInt());
        return TwitterAPI.DATE_FORMAT.format(d);
    }

    // Currently, each tweet contributes around 20 triples.
    private static Tweet randomTweet() throws JSONException, TweetParseException {
        String text = randomString(30, 140);
        String createdAt = randomDateString();
        String profileImageUrl = randomUrl();
        String description = randomString(30, 140);
        String location = randomString(15, 50);
        String screenName = randomString(5, 20);
        String url = randomUrl();
        String name = randomString(10, 30);
        int userId = randomInteger(1000000, 100000000);
        long tweetId = RANDOM.nextInt();

        StringBuilder sb = new StringBuilder();
        sb.append("{\n" +
                "    \"coordinates\":null,\n" +
                "    \"in_reply_to_user_id\":null,\n" +
                "    \"text\":\"").append(text).append("\",\n" +
                "    \"contributors\":null,\n" +
                "    \"favorited\":false,\n" +
                "    \"created_at\":\"").append(createdAt).append("\",\n" +
                "    \"source\":\"<a href=\\\"http://twitterrific.com\\\" rel=\\\"nofollow\\\">Twitterrific</a>\",\n" +
                "    \"geo\":null,\n" +
                "    \"in_reply_to_status_id\":null,\n" +
                "    \"truncated\":false,\n" +
                "    \"place\":null,\n" +
                "    \"in_reply_to_screen_name\":null,\n" +
                "    \"user\":\n" +
                "    {\n" +
                "        \"profile_background_image_url\":\"http://a1.twimg.com/profile_background_images/58551286/fish_twitter_background.jpg\",\n" +
                "        \"contributors_enabled\":false,\n" +
                "        \"profile_background_color\":\"1A1B1F\",\n" +
                "        \"profile_background_tile\":true,\n" +
                "        \"created_at\":\"Tue Jun 26 05:26:08 +0000 2007\",\n" +
                "        \"profile_image_url\":\"").append(profileImageUrl).append("\",\n" +
                "        \"profile_text_color\":\"666666\",\n" +
                "        \"followers_count\":110,\n" +
                "        \"description\":\"").append(description).append("\",\n" +
                "        \"lang\":\"en\",\n" +
                "        \"verified\":false,\n" +
                "        \"location\":\"").append(location).append("\",\n" +
                "        \"screen_name\":\"").append(screenName).append("\",\n" +
                "        \"following\":null,\n" +
                "        \"friends_count\":93,\n" +
                "        \"profile_link_color\":\"2FC2EF\",\n" +
                "        \"notifications\":null,\n" +
                "        \"favourites_count\":18,\n" +
                "        \"profile_sidebar_fill_color\":\"252429\",\n" +
                "        \"protected\":false,\n" +
                "        \"url\":\"").append(url).append("\",\n" +
                "        \"name\":\"").append(name).append("\",\n" +
                "        \"geo_enabled\":true,\n" +
                "        \"time_zone\":\"Eastern Time (US & Canada)\",\n" +
                "        \"profile_sidebar_border_color\":\"181A1E\",\n" +
                "        \"id\":").append(userId).append(",\n" +
                "        \"statuses_count\":550,\n" +
                "        \"utc_offset\":-18000\n" +
                "    },\n" +
                "    \"id\":").append(tweetId).append("\n" +
                "}");
        return new Tweet(new JSONObject(sb.toString()));
    }

    private static void stressTest(final Handler<Tweet, TweetHandlerException> handler,
                                   final long chunkSize) throws JSONException, TweetParseException, TweetHandlerException {
        while (true) {
            long before = new Date().getTime();
            for (long i = 0; i < chunkSize; i++) {
                Tweet t = randomTweet();
                if (!handler.handle(t)) {
                    return;
                }
            }
            long duration = new Date().getTime() - before;
            System.out.println("" + chunkSize + " tweets in " + duration + "ms (" + (chunkSize * 1000 / duration) + " tweets/s)");
        }
    }
}