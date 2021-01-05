package io.chrisdavenport.fuuid.doobie.postgres.rig

import doobie._
import org.specs2._
import cats.effect._
import doobie.util.testing._
import org.specs2.specification.core.{ Fragment, Fragments }
import org.specs2.specification.create.{ FormattingFragments => Format }
import org.specs2.specification.dsl.Online._

trait CheckHelper {self : mutable.Specification with CatsResourceIO[Transactor[IO]] => 
  sequential

    private def checkImpl(args: AnalysisArgs): Fragments = {
      // continuesWith is necessary to make sure the query doesn't run too early
      s"${args.header}\n\n${args.cleanedSql.padLeft("  ").toString}\n" >> ok.continueWith{
        val report = getA.flatMap(analyzeIO(args, _)).unsafeRunSync()
          indentBlock(
            report.items.map { item =>
              item.description ! item.error.fold(ok) {
                err => ko(err.wrap(70).toString)
              }
            }
          )
      }
    }

    private def indentBlock(fs: Seq[Fragment]): Fragments =
      // intersperse fragments with newlines, and indent them.
      // This differs from standard version (FragmentsDsl.fragmentsBlock()) in
      // that any failure gets properly indented, too.
      Fragments.empty
        .append(Format.t)
        .append(fs.flatMap(Seq(Format.br, _)))
        .append(Format.bt)

  def check[A: Analyzable](a: A): Fragments =
      checkImpl(Analyzable.unpack(a))

}
