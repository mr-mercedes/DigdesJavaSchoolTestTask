package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {

            List<Map<String, Object>> result1 = starter.execute("INseRT VALUES 'laSTName' = 'Федоров' , 'id'=3, 'age'=15, 'active'=true");
            List<Map<String, Object>> updateOr = starter.execute("updAte VALUES 'cOSt'=10 Where 'iD'=3 or 'cost'=11");
            List<Map<String, Object>> test= starter.execute("INSERT VALUES 'lastName' = 'Иванов' , 'id'=2, 'cost'=13, 'agE'=92");
            List<Map<String, Object>> updateError = starter.execute("UPDATE VALUES 'age'=null where 'id'=3 or 'cost'=13");

            List<Map<String, Object>> result2 = starter.execute("INSERT VALUES 'lastName' = 'Иванов' , 'id'=2, 'age'=40, 'cost'=13");
            List<Map<String, Object>> updateOr2 = starter.execute("UPDATE VALUES 'active'=false where 'id'=3 or 'active'=null");

            List<Map<String, Object>> result3 = starter.execute("INSERT VALUES 'lastName' = 'Георгиев' , 'id'=1, 'age'=30, 'cost'=10");
            List<Map<String, Object>> updateAnd = starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'cost'<13 and 'age'=30");

            List<Map<String, Object>> delete = starter.execute("DELETE WHERE 'id'=3 or 'cost'=19");
            List<Map<String, Object>> result4 = starter.execute("INSERT VALUES 'lAstName' = 'Петров' , 'id'=3, 'age'=20, 'cost'=11 'active'=true" );
            List<Map<String, Object>> select = starter.execute("SELECT WHERE 'id'=3 and 'active'=true");

            List<Map<String, Object>> updateAll = starter.execute("UPDATE VALUES 'cost' = 111");


            List<Map<String, Object>> select2 = starter.execute("SELECT WHERE ‘age’>=30 and ‘lastName’ ilike ‘%а%’");
            List<Map<String, Object>> select3 = starter.execute("SELECT WHERE ‘lastName’ ilike ‘%р%’");
            List<Map<String, Object>> update2 = starter.execute("UPDATE VALUES 'active'=false, 'cost'=99 where 'age'=20");
            List<Map<String, Object>> result6 = starter.execute("UPDATE VALUES 'age'=10, 'active'=true where 'lastNAME' = 'Сидоров'");
            List<Map<String, Object>> update = starter.execute("UPDATE VALUES 'cost'=999");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
