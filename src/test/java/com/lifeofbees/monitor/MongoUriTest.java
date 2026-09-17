package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

class MongoUriTest {

    

    @Test
    void showMongoUri() {
        System.out.println("######## MONGO URI TEST ########");
        String uri = System.getenv("MONGODB_URI");

        System.out.println("**************** MONGODB TEST ****************");
        System.out.println("MONGODB_URI set: " + (uri != null));

        if (uri != null) {
            System.out.println("MONGODB_URI starts correctly: "
                    + (uri.startsWith("mongodb://")
                    || uri.startsWith("mongodb+srv://")));

            System.out.println("First character code: "
                    + (int) uri.charAt(0));

            System.out.println("Length: " + uri.length());
        }
    }
}