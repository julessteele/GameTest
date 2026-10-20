package entity.flags

class EntityFlags {

    private var flags:MutableList<EntityFlagTypes> = mutableListOf()

    fun addFlag(flag:EntityFlagTypes) = flags.add(flag)
    fun removeFlag(flag:EntityFlagTypes) = flags.remove(flag)
    fun findFlag(flag:EntityFlagTypes):EntityFlagTypes? = flags.find { it == flag }
}