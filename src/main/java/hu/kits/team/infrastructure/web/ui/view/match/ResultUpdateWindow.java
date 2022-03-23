package hu.kits.team.infrastructure.web.ui.view.match;

import java.util.Optional;
import java.util.function.Consumer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import hu.kits.team.domain.MatchResult;
import hu.kits.team.infrastructure.web.ui.component.util.FontSize;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;

class ResultUpdateWindow extends Dialog {

    private final TextField scoredGoalsField = createGoalsField();
    private final TextField concededGoalsField = createGoalsField();
    
    private final Button button = UIUtils.createPrimaryButton("Mentés");
    
    ResultUpdateWindow(Optional<MatchResult> matchResult, Consumer<Optional<MatchResult>> callback) {
        
        if(matchResult.isPresent()) {
            scoredGoalsField.setValue(String.valueOf(matchResult.get().goalsScored()));
            concededGoalsField.setValue(String.valueOf(matchResult.get().goalsConceded()));
        }
        
        HorizontalLayout goalsLayout = new HorizontalLayout(scoredGoalsField, new H3(":"), concededGoalsField);
        goalsLayout.setAlignItems(Alignment.CENTER);
        
        VerticalLayout layout = new VerticalLayout(goalsLayout, button);
        
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(false);
        
        add(layout);
        
        button.addClickListener(click -> {
            Optional<Integer> ourGoals = getNumberOfGoals(scoredGoalsField);
            Optional<Integer> theirGoals = getNumberOfGoals(concededGoalsField);
            Optional<MatchResult> result = ourGoals.isPresent() && theirGoals.isPresent() ? Optional.of(new MatchResult(ourGoals.get(), theirGoals.get())) : Optional.empty();
            callback.accept(result);
            close();
        });
        
        scoredGoalsField.focus();
    }
    
    private static Optional<Integer> getNumberOfGoals(TextField goalField) {
        try {
            return Optional.of(Integer.parseInt(goalField.getValue()));    
        } catch(Exception ex) {
            return Optional.empty();
        }
    }

    private static TextField createGoalsField() {
        TextField goalsField = new TextField();
        goalsField.setWidth("60px");
        goalsField.setPattern("[0-9]*");
        goalsField.setAutoselect(true);
        goalsField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        UIUtils.setFontSize(FontSize.L, goalsField);
        return goalsField;
    }
    
}
