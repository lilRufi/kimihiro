package com.avairebot.commands.utility;

import com.avairebot.AvaIre;
import com.avairebot.contracts.commands.Command;
import com.avairebot.factories.MessageFactory;
import com.udojava.evalex.Expression;
import net.dv8tion.jda.core.entities.Message;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CalculateCommand extends Command {

    public CalculateCommand(AvaIre avaire) {
        super(avaire);
    }

    @Override
    public String getName() {
        return "Calculate Command";
    }

    @Override
    public String getDescription() {
        return "Calculates the given math equations and returns the result for you.";
    }

    @Override
    public List<String> getUsageInstructions() {
        return Collections.singletonList("`:command <equation>` - Calculates the result of the given math equation.");
    }

    @Override
    public List<String> getExampleUsage() {
        return Collections.singletonList("`:command (-50 + sqrt(50 ^ 2 - ((4 * 5) * (100 - 955)))) / (2 * 5) == 9`");
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("calculate", "calc");
    }

    @Override
    public boolean onCommand(Message message, String[] args) {
        if (args.length == 0) {
            return sendErrorMessage(message, "Missing argument, the `equation` argument is required!");
        }

        String string = String.join(" ", args).trim();

        try {
            Expression expression = createExpression(string);
            BigDecimal result = expression.eval();

            if (expression.isBoolean()) {
                MessageFactory.makeInfo(message,
                    generateEasterEgg(expression, result, result.toPlainString(), result.intValueExact() == 1 ? "True" : "False")
                ).queue();
                return true;
            }

            MessageFactory.makeInfo(message, generateEasterEgg(expression, result, result.toPlainString(), string)).queue();
        } catch (Exception ex) {
            return sendErrorMessage(message, ex.getMessage().replaceAll("'", "`"));
        }
        return true;
    }

    private Expression createExpression(String string) {
        int where = string.toLowerCase().indexOf("where");

        if (where == -1) {
            return new Expression(string)
                .setVariable("tau", new BigDecimal(Math.PI * 2));
        }

        Expression expression = new Expression(string.substring(0, where).trim())
            .setVariable("tau", new BigDecimal(Math.PI * 2));

        for (String var : string.substring(where + 5, string.length()).trim().split(" and ")) {
            String[] varArgs = var.split("=");
            if (varArgs.length != 2) {
                varArgs = var.split("is");
                if (varArgs.length != 2) {
                    continue;
                }
            }

            expression.setVariable(varArgs[0].trim(), new BigDecimal(varArgs[1].trim()));
        }

        return expression;
    }

    private String generateEasterEgg(Expression expression, BigDecimal result, String query, String stringifiedResult) {
        if (result.intValueExact() == 69) {
            return stringifiedResult + "\t( ͡° ͜ʖ ͡°)";
        }

        query = query.replaceAll(" ", "");

        if (query.startsWith("2+2-1") && ((expression.isBoolean() && result.intValueExact() == 1) || result.intValueExact() == 3)) {
            return stringifiedResult + "\t-\tQuick maths!";
        }

        if (query.equals("10") && stringifiedResult.equals("10")) {
            return "There are only 10 types of people in the world, those who understand binary and those who don't.";
        }

        return stringifiedResult;
    }
}