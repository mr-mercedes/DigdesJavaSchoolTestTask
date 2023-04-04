package com.digdes.school;

import java.util.*;

import static com.digdes.school.Table.TABLE;


public class JavaSchoolStarter {

    private final Insert insert = new Insert();
    private final Update update = new Update();
    private final Delete delete = new Delete();
    private final Select select = new Select();
    private final Util util = new Util();

    public JavaSchoolStarter() {
    }


    public List<Map<String, Object>> execute(String request) throws Exception {
        List<String> tokens = util.getTokens(request);
        String operation = tokens.get(0);
        switch (operation) {
            case "INSERT" -> insert.insert(tokens);
            case "UPDATE" -> update.update(tokens);
            case "DELETE" -> delete.delete(tokens);
            case "SELECT" -> {
                return select.select(tokens);
            }
            default -> throw new RuntimeException("Unknown operation " + operation);
        }
        return TABLE;
    }
}
