package org.creator.realization.ghaction;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.creator.core.resource.Resource;
import org.creator.core.resource.ResourceType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@Builder
public class DockerHubImage implements Resource {
    private final static boolean ABBREVIATE_TAGS = false;

    private List<String> tags;
    private String path;

    @Override
    public Matcher<String> matcher() {
        return Matchers.equalTo(path);
    }

    @Override
    public String getName() {
        return tags.stream()
                .findFirst()
                .map(this::maybeAbbreviateTag)
                .orElse("UNKNOWN");
    }

    @Override
    public ResourceType getType() {
        return ResourceType.RELIC;
    }

    @Override
    public Map<String, String> getDefinitions() {
        Map<String, String> definitions = IntStream.range(0, tags.size())
                .boxed()
                .collect(Collectors.toMap(i -> "tag" + (i + 1), i -> maybeAbbreviateTag(tags.get(i))));

        definitions.put("path", path);
        return definitions;
    }

    private String maybeAbbreviateTag(String tag) {
        return ABBREVIATE_TAGS ? StringUtils.substringAfterLast(tag, "/") : tag;
    }
}
