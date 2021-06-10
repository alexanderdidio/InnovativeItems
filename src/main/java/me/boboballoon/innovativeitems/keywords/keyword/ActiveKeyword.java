package me.boboballoon.innovativeitems.keywords.keyword;

/**
 * A class that represents a keyword after being parsed
 */
public class ActiveKeyword {
    private final Keyword base;
    private final KeywordContext context;

    /**
     * A constructor used to build a keyword after being parsed
     *
     * @param base the base keyword being used
     * @param context the context in which the base keyword was used in
     */
    public ActiveKeyword(Keyword base, KeywordContext context) {
        this.base = base;
        this.context = context;
    }

    /**
     * A method that executes the base keyword given the provided context
     */
    public void execute() {
        this.base.execute(this.context);
    }
}
