package net.coderbot.iris.gui;

import java.util.ArrayDeque;
import java.util.Deque;

import lombok.Getter;
import net.coderbot.iris.gui.element.ShaderPackOptionList;
import net.coderbot.iris.shaderpack.option.menu.OptionMenuContainer;

public class NavigationController {
	private final OptionMenuContainer container;
	private ShaderPackOptionList optionList;

	@Getter
    private String currentScreen = null;
	private final Deque<String> history = new ArrayDeque<>();

	public NavigationController(OptionMenuContainer container) {
		this.container = container;
	}

	public void back() {
		if (!history.isEmpty()) {
			history.removeLast();

			if (!history.isEmpty()) {
				currentScreen = history.getLast();
			} else {
				currentScreen = null;
			}
		} else {
			currentScreen = null;
		}

		this.rebuild();
	}

	public void open(String screen) {
		currentScreen = screen;
		history.addLast(screen);

		this.rebuild();
	}

	public void rebuild() {
		if (optionList != null) {
			optionList.rebuild();
		}
	}

	public void refresh() {
		if (optionList != null) {
			optionList.refresh();
		}
	}

	public boolean hasHistory() {
		return !this.history.isEmpty();
	}

	public void setActiveOptionList(ShaderPackOptionList optionList) {
		this.optionList = optionList;
	}

}
