package com.revature.rideshare.user.json;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

/**
 * A {@link BeanDeserializerModifier} that looks for {@link JsonLink}
 * annotations and marks corresponding fields for conversion using the
 * {@link LinkDeserializer}.
 */
public class LinkDeserializerModifier extends BeanDeserializerModifier {
	private ApplicationContext context;

	/**
	 * Constructs a new {@link LinkDeserializerModifier} using the given
	 * {@link ApplicationContext} to look up the necessary {@link LinkResolver}.
	 * 
	 * @param context the {@link ApplicationContext} to be used to look up
	 *                {@link LinkResolver} beans.
	 */
	public LinkDeserializerModifier(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
			BeanDeserializerBuilder builder) {
		// Collect a list of all properties to be changed.
		List<SettableBeanProperty> toChange = new ArrayList<>();
		builder.getProperties().forEachRemaining(prop -> {
			JsonLink jsonLink = prop.getAnnotation(JsonLink.class);
			if (jsonLink != null) {
				LinkResolver<? extends Linkable> resolver = context.getBean(jsonLink.value());
				LinkDeserializer<? extends Linkable> deserializer = new LinkDeserializer<>(prop.getType(), resolver);
				toChange.add(prop.withValueDeserializer(deserializer));
			}
		});

		toChange.forEach(prop -> builder.addOrReplaceProperty(prop, true));
		return builder;
	}
}
