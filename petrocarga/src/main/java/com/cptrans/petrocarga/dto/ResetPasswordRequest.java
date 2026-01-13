package com.cptrans.petrocarga.dto;

public record ResetPasswordRequest(String email, String code, String novaSenha) {
}
