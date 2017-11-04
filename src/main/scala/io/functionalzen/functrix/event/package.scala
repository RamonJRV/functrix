package io.functionalzen.functrix

package object event {

  sealed trait Event

  sealed trait CacheEvent extends Event

  final object CacheHitEvent extends CacheEvent

  final object CacheMissEvent extends CacheEvent

  final object CacheUpdateEvent extends CacheEvent

  sealed trait CircuitBreakerEvent extends Event

  final object CircuitBreakerOpenEvent extends CircuitBreakerEvent

  final object CircuitBreakerClosedEvent extends CircuitBreakerEvent

  sealed trait DelayEvent extends Event

  final object ShouldDelayEvent extends DelayEvent

  final object ShouldNotDelayEvent extends DelayEvent

  final object FallbackEvent extends Event

  sealed trait RetryEvent extends Event

  final object ShouldRetryEvent extends Event

  final object ShouldNotRetryEvent extends Event

  final object TimeoutEvent extends Event

}//end package object event

//31337

