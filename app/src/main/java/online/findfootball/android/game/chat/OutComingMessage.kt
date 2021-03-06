package online.findfootball.android.game.chat

/**
 * Created by WiskiW on 24.06.2017.
 */
class OutComingMessage(msg: MessageObj) : MessageObj() {

    enum class SendStatus {
        IN_PROGRESS, OK, ERROR
    }

    var sendStatus: SendStatus = SendStatus.IN_PROGRESS

    init {
        text = msg.text
        userFrom = msg.userFrom
        time = msg.time
    }

    override fun equals(other: Any?): Boolean {
        if (other is OutComingMessage) {
            return super.equals(other)
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return super.hashCode() * 3
    }
}