package views
package html.simul

import lidraughts.api.Context
import lidraughts.app.templating.Environment._
import lidraughts.app.ui.ScalatagsTemplate._

import controllers.routes

object allCreated {

  def apply(simuls: List[lidraughts.simul.Simul]) =
    table(cls := "tournaments")(
      simuls map { simul =>
        tr(
          td(cls := "name")(
            a(cls := "text", href := routes.Simul.show(simul.id))(
              simul.perfTypes map { pt =>
                span(dataIcon := pt.iconChar)
              },
              simul.fullName
            )
          ),
          td(userIdLink(simul.hostId.some)),
          td(cls := "text", dataIcon := "p")(simul.clock.config.show),
          td(cls := "text", dataIcon := "r")(simul.applicants.size),
          td(a(href := routes.Simul.show(simul.id), cls := "button", dataIcon := "G"))
        )
      }
    )
}