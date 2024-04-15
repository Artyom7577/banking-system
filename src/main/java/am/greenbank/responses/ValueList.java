package am.greenbank.responses;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class ValueList implements Value {
    private final List<? extends Value> values;
}
