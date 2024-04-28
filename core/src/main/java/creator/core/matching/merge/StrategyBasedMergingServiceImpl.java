package creator.core.matching.merge;

import lombok.extern.slf4j.Slf4j;
import creator.core.resource.Relic;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class StrategyBasedMergingServiceImpl implements MergingService {
    // TODO: make strategy per resource?
    private final MergeStrategy strategy;

    public StrategyBasedMergingServiceImpl(MergeStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public List<Relic> merge(List<Relic> resources) {
        Map<Relic, List<Relic>> objectGroups = new HashMap<>();

        for (Relic obj : resources) {
            boolean foundGroup = false;
            for (Map.Entry<Relic, List<Relic>> entry : objectGroups.entrySet()) {
                Relic key = entry.getKey();
                if (areMergeable(obj, key)) {
                    entry.getValue().add(obj);
                    foundGroup = true;
                    break;
                }
            }
            if (!foundGroup) {
                List<Relic> newGroup = new ArrayList<>();
                newGroup.add(obj);
                objectGroups.put(obj, newGroup);
            }
        }

        return objectGroups.values().stream()
                .collect(Collectors.partitioningBy(group -> group.size() > 1)) // Partition into groups with more than 1 element
                .entrySet().stream() // Convert the partitioning map into a stream of Map.Entry
                .flatMap(entry -> {
                    if (entry.getKey()) {
                        // Apply 'merge' to groups with more than 1 element
                        return entry.getValue().stream().map(strategy::merge);
                    } else {
                        // Keep single-element groups
                        return entry.getValue().stream().map(group -> group.get(0));
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean areMergeable(Relic left, Relic right) {
        return strategy.mergeable(left, right);
    }
}
