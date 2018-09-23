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
 * A {@link BeanDeserializerModifier} that looks for {@link JsonEnumLike}
 * annotations and marks corresponding fields for conversion using the
 * {@link EnumLikeDeserializer}.
 */
public class EnumLikeDeserializerModifier extends BeanDeserializerModifier {
	private ApplicationContext context;

	/**
	 * Constructs a new {@link EnumLikeDeserializerModifier} using the given
	 * {@link ApplicationContext} to look up the necessary {@link EnumLikeResolver}.
	 * 
	 * @param context the {@link ApplicationContext} to be used to look up
	 *                {@link EnumLikeResolver} beans.
	 */
	public EnumLikeDeserializerModifier(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
			BeanDeserializerBuilder builder) {
		// Collect a list of all properties to be changed.
		List<SettableBeanProperty> toChange = new ArrayList<>();
		builder.getProperties().forEachRemaining(prop -> {
			JsonEnumLike jsonEnumLike = prop.getAnnotation(JsonEnumLike.class);
			if (jsonEnumLike != null) {
				EnumLikeResolver<? extends EnumLike> resolver = context.getBean(jsonEnumLike.value());
				EnumLikeDeserializer<? extends EnumLike> deserializer = new EnumLikeDeserializer<>(prop.getType(),
						resolver);
				toChange.add(prop.withValueDeserializer(deserializer));
			}
		});

		toChange.forEach(prop -> builder.addOrReplaceProperty(prop, true));
		return builder;
	}
}
