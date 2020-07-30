package eu.acolombo.plukke

import android.content.ClipData

/**
 * Returns the item at [index].
 *
 * @throws IndexOutOfBoundsException if index is less than 0 or greater than or equal to the count.
 */
operator fun ClipData.get(index: Int) =
    getItemAt(index) ?: throw IndexOutOfBoundsException("Index: $index, Size: $itemCount")

/** Returns the number of items in this ClipData. */
inline val ClipData.size get() = itemCount

/** Returns true if this ClipData contains no items. */
inline fun ClipData.isEmpty() = itemCount == 0

/** Returns true if this ClipData contains one or more items. */
inline fun ClipData.isNotEmpty() = itemCount != 0

/** Performs the given action on each item in this ClipData. */
inline fun ClipData.forEach(action: (item: ClipData.Item) -> Unit) {
    for (index in 0 until itemCount) {
        action(getItemAt(index))
    }
}

/** Performs the given action on each item in this ClipData, providing its sequential index. */
inline fun ClipData.forEachIndexed(action: (index: Int, item: ClipData.Item) -> Unit) {
    for (index in 0 until itemCount) {
        action(index, getItemAt(index))
    }
}

/** Returns a [MutableIterator] over the items in this ClipData. */
operator fun ClipData.iterator() = object : Iterator<ClipData.Item> {
    private var index = 0
    override fun hasNext() = index < itemCount
    override fun next() = getItemAt(index++) ?: throw IndexOutOfBoundsException()
}

/** Returns a [Sequence] over the items in this ClipData. */
val ClipData.items: Sequence<ClipData.Item>
    get() = object : Sequence<ClipData.Item> {
        override fun iterator() = this@items.iterator()
    }