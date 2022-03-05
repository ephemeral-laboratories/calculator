package garden.ephemeral.calculator.nodes

abstract class BaseBranchNode : Node {
    abstract fun attributesForToString(): Map<String, Any>

    abstract fun childrenForToString(): List<Node>

    final override fun toString(): String {
        val attributes = attributesForToString()
        val children = childrenForToString()
        return buildString {
            append(this@BaseBranchNode::class.simpleName)
            if (attributes.isNotEmpty()) {
                append('[')
                attributes
                    .map { (name, value) -> "$name=$value" }
                    .joinTo(this)
                append(']')
            }
            if (attributes.isNotEmpty() && children.isNotEmpty()) {
                append(' ')
            }
            if (children.isNotEmpty()) {
                append("{\n")
                children.joinTo(this, ",\n") { child -> child.toString().prependIndent() }
                append("\n}")
            }
        }
    }
}