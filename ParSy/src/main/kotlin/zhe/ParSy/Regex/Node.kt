package zhe.ParSy.Regex

class Node(
    private val id: Int,
    val rule: String,
    val parents: Set<Node>,
    val level: Int,
) {
    fun isTop(): Boolean {
	println("I have rule ${rule}, and id ${id}")
	return this.id == 0
    }

    fun allParents(): Set<Node> {
	if (this.isTop()) {
	    return setOf(this)
	}
	var parents = mutableSetOf<Node>(this)
	this.parents.forEach {
	    parents += it.allParents()
	}
	return parents
    }
}
