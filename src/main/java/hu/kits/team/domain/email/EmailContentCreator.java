package hu.kits.team.domain.email;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import hu.kits.team.Main;
import hu.kits.team.common.Formatters;
import hu.kits.team.common.Util;
import hu.kits.team.domain.MatchData;
import hu.kits.team.domain.Member;

class EmailContentCreator {

    static String createReminderEmailContent(Member member, MatchData matchData) {
        String templatePath = "email/reminder.template";
        try(InputStream is = EmailContentCreator.class.getClassLoader().getResourceAsStream(templatePath)){
            String template = IOUtils.toString(is, StandardCharsets.UTF_8.name());
            return fillTemplate(template, createDataMap(member, matchData));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static String fillTemplate(String template, Map<String, Object> valuesToFill) {
        
        Pattern pattern = Pattern.compile("\\$\\{[^\\}]*\\}");
        Matcher matcher = pattern.matcher(template);
        
        String content = template;
        while (matcher.find()) {
            String reference = matcher.group(0);
            String key = reference.substring(2, reference.length()-1);
            content = content.replaceAll("\\$\\{" + key + "\\}", valuesToFill.getOrDefault(key, "").toString());
        }
        
        return content;
    }
    
    private static Map<String, Object> createDataMap(Member member, MatchData matchData) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("nickName", Util.withCapital(member.id));
        map.put("matchId", matchData.id);
        map.put("opponent", matchData.opponent);
        map.put("venue", matchData.venue);
        map.put("matchDay", Formatters.formatWeekday(matchData.time));
        map.put("time", Formatters.formatDateTime(matchData.time));
        map.put("url", Main.URL + "/match/" + matchData.id);
        
        return map;
    }
    
}
