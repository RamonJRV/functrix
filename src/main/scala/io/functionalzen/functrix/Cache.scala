package io.functionalzen.functrix

import io.functionalzen.functrix.Functrix.FunctrixOutput
import io.functionalzen.functrix.event.{CacheHitEvent, CacheMissEvent, CacheUpdateEvent}
import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

object Cache {

  def fixedSizeCache[I,O](maxCacheSize : Int)
                         (f : (I) => FunctrixOutput[O])
                         (implicit ec : ExecutionContext,
                          em : EventMonitor) : Functrix[I,O] = {

    var finiteQueue = immutable.Queue.empty[(I,O)]
    var cache = immutable.Map.empty[I,O]

    @scala.annotation.tailrec
    def dropElements[E](queue : immutable.Queue[E],
                        dropCount : Int)
                       (implicit em : EventMonitor) : immutable.Queue[E] =
      if(dropCount <= 0)
        queue
      else
        dropElements(queue.dequeue._2, dropCount - 1)

    val updateCache : (I,O) => Unit =
      (i,o) => {
        finiteQueue = finiteQueue enqueue ((i,o))
        if(finiteQueue.size > maxCacheSize) {
          finiteQueue = dropElements(finiteQueue, finiteQueue.size - maxCacheSize + 1)
          cache = finiteQueue.toMap
        }
        cache = cache + (i -> o)
      }

    genericCache[I,O]((i : I) => Future(cache(i)), updateCache)(f)
  }//end def fixedSizeCache

  def genericCache[I,O](queryCache : (I) => Future[O], updateCache : (I, O) => Unit)
                       (f : (I) => FunctrixOutput[O])
                       (implicit ec : ExecutionContext, em : EventMonitor) : Functrix[I,O] =
    (input: I) =>
      queryCache(input)
        .andThen {
          case Success(_) => em update CacheHitEvent
        }
        .recoverWith {
          case _ => { em update CacheMissEvent ; f(input) } andThen {
            case Success(o) =>
              em update CacheUpdateEvent
              updateCache(input, o)
          }
        }


}//end object Cache

//31337
