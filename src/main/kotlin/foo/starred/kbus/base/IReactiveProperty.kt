package foo.starred.kbus.base

interface IReactiveProperty<T> {
    val value: T

    fun observe(callback: (T) -> Unit): IReactiveProperty<T>
}
