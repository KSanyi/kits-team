package hu.kits.team.infrastructure.db;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Update;

import hu.kits.team.common.CollectionsUtil;

public class JdbiUtil {

    public static Update createInsert(Handle handle, String tableName, Map<String, ?> values) {
        
        List<String> keys = new ArrayList<>(values.keySet());
        
        String sql = "INSERT INTO " + tableName + "(" + String.join(", ", keys) + ") VALUES (" + createQuestionMarks(keys.size()) + ")";
        
        Update update = handle.createUpdate(sql);
        for(String key : keys) {
            update = update.bind(keys.indexOf(key), values.get(key));
        }
        
        return update;
    }
    
    private static String createQuestionMarks(int n) {
        return IntStream.rangeClosed(1, n).mapToObj(i -> "?").collect(Collectors.joining(", "));
    }
    
    public static Update createUpdate(Handle handle, String tableName, Map<String, Object> originalMap, Map<String, Object> updatedMap, String keyColumn, String keyColumnValue) {
        
        Map<String, Object> paramMap = new HashMap<>();
        List<String> updatesLog = new ArrayList<>();
        
        Set<String> keys = CollectionsUtil.union(originalMap.keySet(), updatedMap.keySet());
        for(String columnName : keys) {
            Object originalValue = originalMap.get(columnName);
            Object updatedValue = updatedMap.get(columnName);
            if(!compare(originalValue, updatedValue)) {
                String change = String.format("%s: %s -> %s ", columnName, originalValue, updatedValue);
                updatesLog.add(change);
                paramMap.put(columnName, updatedValue);
            }
        }
        
        String updateSql = String.format("UPDATE %s SET %s WHERE %s = :id", tableName, paramMap.keySet().stream().map(column -> column + " = :" + column).collect(joining(", ")), keyColumn);
        paramMap.put("id", keyColumnValue);
        return handle.createUpdate(updateSql).bindMap(paramMap);
    }
    
    private static boolean compare(Object a, Object b) {
        if(a == null && b == null) return true;
        if(a != null && b == null || a == null && b != null) return false;
        return a.equals(b);
    }
    
}
