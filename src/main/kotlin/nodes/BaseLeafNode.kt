package garden.ephemeral.calculator.nodes

abstract class BaseLeafNode : Node {
    abstract fun attributesForToString(): Map<String, Any>

    final override fun toString(): String {
        val attributes = attributesForToString()
        return buildString {
            append(this@BaseLeafNode::class.simpleName)
            if (attributes.isNotEmpty()) {
                append('[')
                attributes.forEach { (name, value) ->
                    append(name).append("=").append(value)
                }
                append(']')
            }
        }
    }
}
