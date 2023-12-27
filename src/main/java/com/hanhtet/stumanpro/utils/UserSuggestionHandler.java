package com.hanhtet.stumanpro.utils;

import com.hanhtet.stumanpro.entity.Course;
import com.hanhtet.stumanpro.entity.User;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class UserSuggestionHandler<T> {

  private VBox suggestionContainer;
  private ScrollPane scrollPane;

  public UserSuggestionHandler(
    VBox suggestionContainer,
    ScrollPane scrollPane
  ) {
    this.suggestionContainer = suggestionContainer;
    this.scrollPane = scrollPane;
  }

  public void populateSearchSuggestions(
    ObservableList<T> suggestions,
    OnSuggestionClickListener<T> listener
  ) {
    suggestionContainer.getChildren().clear();
    for (T entity : suggestions) {
      String entityName = getEntityName(entity);
      Label suggestionLabel = new Label(entityName.trim());
      suggestionLabel.getStyleClass().add("label-suggestion");
      suggestionLabel.setMaxWidth(Double.MAX_VALUE);
      suggestionLabel.prefWidthProperty().bind(scrollPane.widthProperty());
      suggestionLabel.setOnMouseClicked(event -> listener.onClick(entity));
      suggestionContainer.getChildren().add(suggestionLabel);
    }
    suggestionContainer.setVisible(true);
  }

  public interface OnSuggestionClickListener<T> {
    void onClick(T selectedEntity);
  }

  public void clearSearchSuggestions() {
    suggestionContainer.getChildren().clear();
  }

  private String getEntityName(T entity) {
    // Logic to get the name of the entity, replace this with your entity-specific logic
    if (entity instanceof User) {
      return ((User) entity).getName();
    } else if (entity instanceof Course) {
      return ((Course) entity).getName();
    }
    return "";
  }
}
