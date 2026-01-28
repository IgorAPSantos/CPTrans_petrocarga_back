# 📧 Configuração Gmail API - POC PetroCarga

## Visão Geral

Este documento explica como configurar a Gmail API para envio de emails
diretamente de uma conta Gmail, sem necessidade de domínio próprio ou SMTP.

**Tempo estimado:** 15-20 minutos

---

## Passo 1: Criar Projeto no Google Cloud Console

1. Acesse: https://console.cloud.google.com/
2. Faça login com a conta Google que será usada para enviar emails
   - Recomendado: `cpt.petrocarga@gmail.com`
3. Clique em **"Selecionar projeto"** (topo da tela)
4. Clique em **"Novo Projeto"**
5. Nome do projeto: `PetroCarga Email`
6. Clique em **"Criar"**

---

## Passo 2: Habilitar Gmail API

1. No menu lateral, vá em: **APIs e Serviços** → **Biblioteca**
2. Pesquise por: `Gmail API`
3. Clique em **Gmail API**
4. Clique em **"Ativar"**

---

## Passo 3: Configurar Tela de Consentimento OAuth

1. No menu lateral, vá em: **APIs e Serviços** → **Tela de consentimento OAuth**
2. Selecione **"Externo"** e clique em **"Criar"**
3. Preencha:
   - **Nome do app:** `PetroCarga`
   - **Email de suporte:** seu email
   - **Emails de contato do desenvolvedor:** seu email
4. Clique em **"Salvar e continuar"**
5. Na tela de **Escopos**, clique em **"Adicionar ou remover escopos"**
6. Procure e selecione: `https://www.googleapis.com/auth/gmail.send`
7. Clique em **"Atualizar"** e depois **"Salvar e continuar"**
8. Na tela de **Usuários de teste**, clique em **"Add users"**
9. Adicione o email que vai ENVIAR os emails: `cpt.petrocarga@gmail.com`
10. Clique em **"Salvar e continuar"**

---

## Passo 4: Criar Credenciais OAuth 2.0

1. No menu lateral, vá em: **APIs e Serviços** → **Credenciais**
2. Clique em **"Criar credenciais"** → **"ID do cliente OAuth"**
3. Tipo de aplicativo: **"Aplicativo da Web"**
4. Nome: `PetroCarga Backend`
5. Em **URIs de redirecionamento autorizados**, adicione:
   - `https://developers.google.com/oauthplayground`
6. Clique em **"Criar"**
7. **COPIE E SALVE:**
   - **Client ID** (algo como: `xxxxx.apps.googleusercontent.com`)
   - **Client Secret** (algo como: `GOCSPX-xxxxx`)

---

## Passo 5: Obter Refresh Token

1. Acesse: https://developers.google.com/oauthplayground
2. Clique no ícone de **engrenagem** (⚙️) no canto superior direito
3. Marque: **"Use your own OAuth credentials"**
4. Cole seu **Client ID** e **Client Secret**
5. Feche as configurações
6. No painel esquerdo, procure **"Gmail API v1"**
7. Expanda e selecione: `https://www.googleapis.com/auth/gmail.send`
8. Clique em **"Authorize APIs"**
9. Faça login com `cpt.petrocarga@gmail.com`
10. Clique em **"Continuar"** (pode aparecer aviso de app não verificado)
11. Clique em **"Exchange authorization code for tokens"**
12. **COPIE E SALVE o "Refresh token"**

---

## Passo 6: Configurar Variáveis no Railway

Acesse o Railway Dashboard e adicione estas variáveis:

| Variável | Valor |
|----------|-------|
| `GMAIL_CLIENT_ID` | (seu Client ID do passo 4) |
| `GMAIL_CLIENT_SECRET` | (seu Client Secret do passo 4) |
| `GMAIL_REFRESH_TOKEN` | (seu Refresh Token do passo 5) |
| `GMAIL_FROM` | `cpt.petrocarga@gmail.com` |

---

## Passo 7: Remover Variáveis Antigas

Remova estas variáveis do Railway (se existirem):
- `SENDGRID_API_KEY`
- `SENDGRID_FROM`
- `RESEND_API_KEY`
- `RESEND_FROM`
- `SMTP_*` (todas)

---

## Passo 8: Testar

Após o redeploy, acesse o Swagger:
```
https://seu-app.railway.app/petrocarga/swagger-ui.html
```

Procure por **"Email Test"** e use:
- `POST /api/test/send-activation`
- `POST /api/test/send-reset`

---

## Logs Esperados (Sucesso)

```
========================================================
  GmailApiEmailService (Gmail API) initializing...
  From: cpt.petrocarga@gmail.com
  Gmail API inicializada com SUCESSO!
  Emails serão enviados autenticamente via Gmail
========================================================
```

---

## Troubleshooting

### "Não foi possível obter access token"
- Verifique se Client ID, Client Secret e Refresh Token estão corretos
- O Refresh Token pode ter expirado (refaça o passo 5)

### "Gmail API não inicializada"
- Verifique se `GMAIL_REFRESH_TOKEN` está configurado no Railway
- Verifique os logs de inicialização

### App não verificado
- Normal para POC/desenvolvimento
- Clique em "Avançado" → "Acessar PetroCarga (não seguro)"

---

## Limitações

| Aspecto | Limite |
|---------|--------|
| Emails/dia (conta pessoal) | 100 |
| Emails/dia (Workspace) | 2.000 |
| Refresh token | Expira após 6 meses sem uso |
| Produção | NÃO recomendado em escala |

---

## Evolução para Produção

Quando o projeto evoluir:
1. Adquirir domínio próprio
2. Configurar SPF, DKIM, DMARC
3. Usar SendGrid, AWS SES ou similar
4. Substituir `GmailApiEmailService` por implementação SMTP
5. A interface `EmailSender` facilita essa troca
