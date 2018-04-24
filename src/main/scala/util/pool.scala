package util

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

object pool{
  implicit lazy val global:ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(20))
}
