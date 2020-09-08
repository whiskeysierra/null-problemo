package org.zalando.problem;

import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807: Problem Details for HTTP APIs</a>
 * 
 * {@link Problem} instances are required to be immutable.
 */
@API(status = STABLE)
public interface Problem {

    URI DEFAULT_TYPE = URI.create("about:blank");

    /**
     * An absolute URI that identifies the problem type. When dereferenced,
     * it SHOULD provide human-readable documentation for the problem type
     * (e.g., using HTML). When this member is not present, its value is
     * assumed to be "about:blank".
     *
     * @return an absolute URI that identifies this problem's type
     */
    default URI getType() {
        return DEFAULT_TYPE;
    }

    /**
     * A short, human-readable summary of the problem type. It SHOULD NOT
     * change from occurrence to occurrence of the problem, except for
     * purposes of localisation.
     *
     * @return a short, human-readable summary of this problem
     */
    @Nullable
    default String getTitle() {
        return null;
    }

    /**
     * The HTTP status code generated by the origin server for this
     * occurrence of the problem.
     *
     * @return the HTTP status code
     */
    @Nullable
    default StatusType getStatus() {
        return null;
    }

    /**
     * A human readable explanation specific to this occurrence of the problem.
     *
     * @return A human readable explaination of this problem
     */
    @Nullable
    default String getDetail() {
        return null;
    }

    /**
     * An absolute URI that identifies the specific occurrence of the problem.
     * It may or may not yield further information if dereferenced.
     *
     * @return an absolute URI that identifies this specific problem
     */
    @Nullable
    default URI getInstance() {
        return null;
    }

    /**
     * Optional, additional attributes of the problem. Implementations can choose to ignore this in favor of concrete,
     * typed fields.
     *
     * @return additional parameters
     */
    default Map<String, Object> getParameters() {
        return Collections.emptyMap();
    }

    static ProblemBuilder builder() {
        return new ProblemBuilder();
    }

    static ThrowableProblem valueOf(final StatusType status) {
        return GenericProblems.create(status).build();
    }

    static ThrowableProblem valueOf(final StatusType status, final String detail) {
        return GenericProblems.create(status).withDetail(detail).build();
    }

    static ThrowableProblem valueOf(final StatusType status, final URI instance) {
        return GenericProblems.create(status).withInstance(instance).build();
    }

    static ThrowableProblem valueOf(final StatusType status, final String detail, final URI instance) {
        return GenericProblems.create(status).withDetail(detail).withInstance(instance).build();
    }

    /**
     * Specification by example:
     * <pre>{@code
     *   // Returns "about:blank{404, Not Found}"
     *   Problem.valueOf(NOT_FOUND).toString();
     *
     *   // Returns "about:blank{404, Not Found, Order 123}"
     *   Problem.valueOf(NOT_FOUND, "Order 123").toString();
     *
     *   // Returns "about:blank{404, Not Found, instance=https://example.org/}"
     *   Problem.valueOf(NOT_FOUND, URI.create("https://example.org/")).toString();
     *
     *   // Returns "about:blank{404, Not Found, Order 123, instance=https://example.org/"}
     *   Problem.valueOf(NOT_FOUND, "Order 123", URI.create("https://example.org/")).toString();
     *
     *   // Returns "https://example.org/problem{422, Oh, oh!, Crap., instance=https://example.org/problem/123}
     *   Problem.builder()
     *       .withType(URI.create("https://example.org/problem"))
     *       .withTitle("Oh, oh!")
     *       .withStatus(UNPROCESSABLE_ENTITY)
     *       .withDetail("Crap.")
     *       .withInstance(URI.create("https://example.org/problem/123"))
     *       .build()
     *       .toString();
     * }</pre>
     *
     * @param problem the problem
     * @return a string representation of the problem
     * @see Problem#valueOf(StatusType)
     * @see Problem#valueOf(StatusType, String)
     * @see Problem#valueOf(StatusType, URI)
     * @see Problem#valueOf(StatusType, String, URI)
     */
    static String toString(final Problem problem) {
        final Stream<String> parts = Stream.concat(
                Stream.of(
                        problem.getStatus() == null ? null : String.valueOf(problem.getStatus().getStatusCode()),
                        problem.getTitle(),
                        problem.getDetail(),
                        problem.getInstance() == null ? null : "instance=" + problem.getInstance()),
                problem.getParameters()
                        .entrySet().stream()
                        .map(Map.Entry::toString))
                .filter(Objects::nonNull);

        return problem.getType().toString() + "{" + parts.collect(joining(", ")) + "}";
    }

}
