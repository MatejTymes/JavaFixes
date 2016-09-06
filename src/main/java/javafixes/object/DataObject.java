package javafixes.object;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * {@code DataObject} is intended as a base class for domain objects.
 * It adds reflection based methods {@code equals()}, {@code hashCode()}
 * and {@code toString()} so your domain classes can be more readable.
 *
 * @author mtymes
 */
public abstract class DataObject {

    /**
     * Evaluates if object to compare is of the same DataObject type and all non-transient
     * fields are equal as well
     *
     * @param other value to compare to
     * @return {@code true} if other value is of the same DataObject type and all
     * non-transient field are equal. Otherwise returns {@code false}
     */
    @Override
    public boolean equals(Object other) {
        return reflectionEquals(this, other);
    }

    /**
     * Returns hashCode calculated using reflection
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    /**
     * Returns toString calculated using reflection
     *
     * @return toString value
     */
    @Override
    public String toString() {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
