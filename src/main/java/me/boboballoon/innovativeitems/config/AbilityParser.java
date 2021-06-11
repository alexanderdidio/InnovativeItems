package me.boboballoon.innovativeitems.config;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import me.boboballoon.innovativeitems.keywords.keyword.ActiveKeyword;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * A class built for parsing configuration sections and convert into CustomItem objects
 */
public class AbilityParser {
    /**
     * A util method used to parse a custom item from a config section
     *
     * @param raw the raw string array of keywords
     * @param triggerName the provided raw ability trigger
     * @param name the name of the ability
     * @return the ability (null if an error occurred)
     */
    public static Ability parseAbility(List<String> raw, String triggerName, String name) {
        AbilityTrigger trigger = AbilityTrigger.getFromIdentifier(triggerName);

        if (trigger == null) {
            LogUtil.log(Level.WARNING, "There was an error parsing the ability trigger for " + name + ", are you sure " + triggerName + " is a correct trigger?");
            return null;
        }

        List<ActiveKeyword> keywords = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i);

            if (!line.matches("\\w+\\([\\w|,|\\s]*\\)")) { //regex = ^\w+\([\w|,|\s]*\)$ (^ and $ are already put in inside of the match method)
                LogUtil.log(Level.WARNING, "There was an error parsing line " + i + " on ability " + name + "! Did you format it correctly?");
                continue;
            }

            String[] split = line.split("\\("); //regex = \(

            Keyword keyword = InnovativeItems.getInstance().getKeywordManager().getKeyword(split[0]);

            if (keyword == null) {
                LogUtil.log(Level.WARNING, "There was an error parsing line " + i + " on ability " + name + "! Did you use a valid keyword?");
                continue;
            }

            String[] rawArguments = split[1].substring(0, split[1].length() - 1).replaceAll("\\s", "").split(",");

            KeywordContext context = new KeywordContext(rawArguments, name);

            keywords.add(new ActiveKeyword(keyword, context));
        }

        return new Ability(name, keywords, trigger);
    }
}
