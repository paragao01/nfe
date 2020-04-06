/*
 * Decompiled with CFR 0_115.
 */
package br.com.debra.certificado;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum TipoCertificadoEnum {
    REPOSITORIO_WINDOWS("windows"),
    REPOSITORIO_MAC("mac"),
    ARQUIVO("arquivo"),
    ARQUIVO_BYTES("arquivo_bytes"),
    TOKEN_A3("a3");
    
    private final String descricao;

    private TipoCertificadoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public static TipoCertificadoEnum valueOfDescricao(String descricao) {
        return Arrays.stream(TipoCertificadoEnum.values()).filter(x -> x.getDescricao().equals(descricao)).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}

