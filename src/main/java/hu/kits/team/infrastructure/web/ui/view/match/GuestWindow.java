package hu.kits.team.infrastructure.web.ui.view.match;

import java.util.function.Consumer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;

class GuestWindow extends Dialog {

    private final TextField nameField = new TextField("Név");
    
    private final Button button = UIUtils.createPrimaryButton("Mentés");
    
    GuestWindow(Consumer<String> callback) {
        
        VerticalLayout layout = new VerticalLayout(new H3("Vendég játékos"), nameField, button);
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(false);
        
        add(layout);
        
        button.addClickListener(click -> {
            if(!nameField.isEmpty()) {
                callback.accept(nameField.getValue());
                close();
            }
        });
        
        nameField.focus();
    }
    
}
