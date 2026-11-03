// Funções compartilhadas pelas telas de CRUD (serviços e agendamentos)
const $ = id => document.getElementById(id);

// Aviso fixo (erros de validação / servidor)
function alerta(texto, tipo) {
    const el = $("msgTela");
    el.textContent = texto;
    el.className = "alerta " + (tipo || "");
}

// Aviso que aparece e some sozinho (sucesso)
function toast(texto) {
    const t = $("toast");
    t.textContent = texto;
    t.classList.add("mostrar");
    clearTimeout(t._timer);
    t._timer = setTimeout(() => t.classList.remove("mostrar"), 3500);
}

// fetch com tratamento de sessão: 401 -> login, 403 -> início
async function api(url, opcoes) {
    const r = await fetch(url, opcoes);
    if (r.status === 401) { window.location.href = "/index.html"; throw new Error("redirecionado"); }
    if (r.status === 403) { window.location.href = "/home.html"; throw new Error("redirecionado"); }
    const dados = await r.json().catch(() => ({}));   // 204 não tem corpo
    if (!r.ok) throw new Error(dados.mensagem || "Erro " + r.status);
    return dados;
}

function jsonOpcoes(metodo, corpo) {
    return { method: metodo, headers: { "Content-Type": "application/json" }, body: JSON.stringify(corpo) };
}

const moeda = n => Number(n).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
const dataBR = iso => iso ? iso.split("-").reverse().join("/") : "";

// Modal de confirmação (<dialog>): devolve true se a pessoa clicar em "Excluir"
function confirmar(mensagem) {
    return new Promise(resolve => {
        const dlg = $("modalConfirmar");
        $("modalTexto").textContent = mensagem;
        dlg.returnValue = "";
        dlg.onclose = () => resolve(dlg.returnValue === "sim");
        dlg.showModal();
    });
}

// Desafio 2: exporta a tabela para CSV (abre direto no Excel; separador ; e acentos ok)
function exportarCSV(nomeArquivo, cabecalho, linhas) {
    const esc = v => '"' + String(v ?? "").replace(/"/g, '""') + '"';
    const csv = [cabecalho, ...linhas].map(l => l.map(esc).join(";")).join("\r\n");
    const blob = new Blob(["\ufeff" + csv], { type: "text/csv;charset=utf-8" });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = nomeArquivo;
    a.click();
    URL.revokeObjectURL(a.href);
}

// Cria uma célula de tabela com textContent (evita injeção de HTML)
function celula(texto, classe) {
    const td = document.createElement("td");
    td.textContent = texto;
    if (classe) td.className = classe;
    return td;
}

// Células de ação (Editar / Excluir) de uma linha
function celulaAcoes(aoEditar, aoExcluir) {
    const td = document.createElement("td");
    td.className = "col-acoes";
    const ed = document.createElement("button");
    ed.type = "button"; ed.textContent = "Editar"; ed.className = "btn-tabela"; ed.onclick = aoEditar;
    const ex = document.createElement("button");
    ex.type = "button"; ex.textContent = "Excluir"; ex.className = "btn-tabela perigo"; ex.onclick = aoExcluir;
    td.append(ed, ex);
    return td;
}
