package views.html

import play.api.libs.json.Json
import play.twirl.api.Html
import scalatags.Text.RawFrag

import lidraughts.api.Context
import lidraughts.app.templating.Environment._
import lidraughts.i18n.I18nKeys

object chat {

  val html = Html("""<div id="chat" class="side_box"></div>""")
  val frag = RawFrag(html.toString)

  import lidraughts.chat.JsonView.chatIdWrites

  def restrictedJson(
    chat: lidraughts.chat.Chat.Restricted,
    name: String,
    timeout: Boolean,
    public: Boolean, // game players chat is not public
    withNote: Boolean = false,
    writeable: Boolean = true,
    localMod: Boolean = false
  )(implicit ctx: Context) =
    json(
      chat.chat, name = name, timeout = timeout, withNote = withNote, writeable = writeable, public = public, restricted = chat.restricted, localMod = localMod
    )

  def json(
    chat: lidraughts.chat.AnyChat,
    name: String,
    timeout: Boolean,
    public: Boolean, // game players chat is not public
    withNote: Boolean = false,
    writeable: Boolean = true,
    restricted: Boolean = false,
    localMod: Boolean = false
  )(implicit ctx: Context) = Json.obj(
    "data" -> Json.obj(
      "id" -> chat.id,
      "name" -> name,
      "lines" -> lidraughts.chat.JsonView(chat),
      "userId" -> ctx.userId,
      "loginRequired" -> chat.loginRequired,
      "restricted" -> restricted
    ),
    "i18n" -> i18n(withNote = withNote),
    "writeable" -> writeable,
    "noteId" -> withNote.option(chat.id.value take 8),
    "public" -> public,
    "permissions" -> Json.obj("local" -> localMod)
      .add("timeout" -> isGranted(_.ChatTimeout))
      .add("shadowban" -> isGranted(_.MarkTroll)),
    "timeout" -> timeout
  ).add("kobold" -> ctx.troll)
    .add("timeoutReasons" -> isGranted(_.ChatTimeout).option(lidraughts.chat.JsonView.timeoutReasons))

  def i18n(withNote: Boolean)(implicit ctx: Context) = i18nOptionJsObject(
    I18nKeys.talkInChat.some,
    I18nKeys.toggleTheChat.some,
    I18nKeys.loginToChat.some,
    I18nKeys.youHaveBeenTimedOut.some,
    withNote option I18nKeys.notes,
    withNote option I18nKeys.typePrivateNotesHere
  )
}
