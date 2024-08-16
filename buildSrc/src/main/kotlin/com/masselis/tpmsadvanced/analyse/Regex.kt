package com.masselis.tpmsadvanced.analyse

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object RegexSerializer : KSerializer<Regex> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Regex", STRING)
    override fun serialize(encoder: Encoder, value: Regex) = encoder.encodeString(value.pattern)
    override fun deserialize(decoder: Decoder): Regex = Regex(decoder.decodeString())
}
