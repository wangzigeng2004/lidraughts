package lidraughts.tournament

import play.api.libs.json.Json

import lidraughts.hub.actorApi.socket.SendTos
import lidraughts.socket.Socket.makeMessage

private final class TournamentReminder(bus: lidraughts.common.Bus) {

  private val max = 50

  def apply(tour: Tournament, activeUserIds: Seq[lidraughts.user.User.ID]) = if (!tour.pairingsClosed) {
    val userIds = {
      if (activeUserIds.size > max) scala.util.Random.shuffle(activeUserIds) take max
      else activeUserIds
    }.toSet
    if (userIds.nonEmpty) bus.publish(SendTos(userIds, makeMessage(
      "tournamentReminder",
      Json.obj(
        "id" -> tour.id,
        "name" -> tour.fullName
      )
    )), 'socketUsers)
  }
}
