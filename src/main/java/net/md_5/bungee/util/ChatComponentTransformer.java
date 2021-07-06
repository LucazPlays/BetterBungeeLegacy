package net.md_5.bungee.util;

import java.util.regex.*;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.chat.hover.content.*;
import com.google.common.collect.*;
import net.md_5.bungee.api.chat.*;
import java.util.*;
import com.google.common.base.*;
import net.md_5.bungee.api.score.*;

public final class ChatComponentTransformer {
	private static final ChatComponentTransformer INSTANCE;
	private static final Pattern SELECTOR_PATTERN;

	public BaseComponent[] legacyHoverTransform(final ProxiedPlayer player, final BaseComponent... components) {
		if (player.getPendingConnection().getVersion() < 735) {
			for (int i = 0; i < components.length; ++i) {
				BaseComponent next = components[i];
				if (next.getHoverEvent() != null) {
					if (!next.getHoverEvent().isLegacy()) {
						next = next.duplicate();
						next.getHoverEvent().setLegacy(true);
						if (next.getHoverEvent().getContents().size() > 1) {
							final Content exception = next.getHoverEvent().getContents().get(0);
							next.getHoverEvent().getContents().clear();
							next.getHoverEvent().getContents().add(exception);
						}
						components[i] = next;
					}
				}
			}
		}
		return components;
	}

	public static ChatComponentTransformer getInstance() {
		return ChatComponentTransformer.INSTANCE;
	}

	public BaseComponent[] transform(final ProxiedPlayer player, final BaseComponent... components) {
		return this.transform(player, false, components);
	}

	public BaseComponent[] transform(final ProxiedPlayer player, final boolean transformHover,
			BaseComponent... components) {
		if (components == null || components.length < 1 || (components.length == 1 && components[0] == null)) {
			return new BaseComponent[] { new TextComponent("") };
		}
		if (transformHover) {
			components = this.legacyHoverTransform(player, components);
		}
		for (final BaseComponent root : components) {
			if (root.getExtra() != null && !root.getExtra().isEmpty()) {
				final List<BaseComponent> list = Lists.newArrayList(this.transform(player, transformHover,
						(BaseComponent[]) root.getExtra().toArray(new BaseComponent[0])));
				root.setExtra(list);
			}
			if (root instanceof ScoreComponent) {
				this.transformScoreComponent(player, (ScoreComponent) root);
			}
		}
		return components;
	}

	private void transformScoreComponent(final ProxiedPlayer player, final ScoreComponent component) {
		Preconditions.checkArgument(!this.isSelectorPattern(component.getName()),
				(Object) "Cannot transform entity selector patterns");
		if (component.getValue() != null && !component.getValue().isEmpty()) {
			return;
		}
		if (component.getName().equals("*")) {
			component.setName(player.getName());
		}
		if (player.getScoreboard().getObjective(component.getObjective()) != null) {
			final Score score = player.getScoreboard().getScore(component.getName());
			if (score != null) {
				component.setValue(Integer.toString(score.getValue()));
			}
		}
	}

	public boolean isSelectorPattern(final String pattern) {
		return ChatComponentTransformer.SELECTOR_PATTERN.matcher(pattern).matches();
	}

	private ChatComponentTransformer() {
	}

	static {
		INSTANCE = new ChatComponentTransformer();
		SELECTOR_PATTERN = Pattern.compile("^@([pares])(?:\\[([^ ]*)\\])?$");
	}
}
