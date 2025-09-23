package com.loja.loja_api.dto;

import com.loja.loja_api.models.Dimensoes;
import com.loja.loja_api.models.Produto;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {

    private Long id;
    private String nome;
    private String marca;
    private String slug;
    private String descricao;
    private String descricaoCurta;
    private List<String> categorias;
    private List<String> objetivos;
    private String sabor;
    private String tamanhoPorcao;
    private String fornecedor;
    private String statusAprovacao;
    private String disponibilidade;
    private Double peso;
    private Double preco;
    private Double precoDesconto;
    private String porcentagemDesconto;
    private Double custo;
    private Double lucroEstimado;
    private Boolean ativo;
    private Boolean destaque;

    private Integer estoqueTotal; // calculado a partir dos lotes
    private Integer estoqueMinimo;
    private Integer estoqueMaximo;
    private String localizacaoFisica;
    private String codigoBarras;
    private Dimensoes dimensoes;
    private List<String> restricoes;
    private String tabelaNutricional;
    private String modoDeUso;
    private List<String> palavrasChave;
    private Double avaliacaoMedia;
    private List<String> comentarios;
    private LocalDate dataCadastro;
    private LocalDate dataUltimaAtualizacao;
    private LocalDate dataValidade;
    private Long fornecedorId;
    private String cnpjFornecedor;
    private String contatoFornecedor;
    private String prazoEntregaFornecedor;
    private Integer quantidadeVendida;
    private List<Integer> vendasMensais;

    private String imagemMimeType;
    private String imagemBase64;
    private List<String> galeriaMimeTypes;
    private List<String> galeriaBase64;

    public static ProdutoDTO fromEntity(Produto p) {
        int estoqueTotal = (p.getLotes() != null)
                ? p.getLotes().stream()
                .filter(l -> l.getQuantidade() != null) // Proteção contra NPE
                .mapToInt(l -> l.getQuantidade())
                .sum()
                : 0;

        return ProdutoDTO.builder()
                .id(p.getId())
                .nome(p.getNome())
                .marca(p.getMarca())
                .slug(p.getSlug())
                .descricao(p.getDescricao())
                .descricaoCurta(p.getDescricaoCurta())
                .categorias(new ArrayList<>(Optional.ofNullable(p.getCategorias()).orElse(Collections.emptyList())))
                .objetivos(new ArrayList<>(Optional.ofNullable(p.getObjetivos()).orElse(Collections.emptyList())))
                .sabor(p.getSabor())
                .tamanhoPorcao(p.getTamanhoPorcao())
                .fornecedor(p.getFornecedor())
                .statusAprovacao(p.getStatusAprovacao())
                .disponibilidade(p.getDisponibilidade())
                .peso(p.getPeso())
                .preco(p.getPreco())
                .precoDesconto(p.getPrecoDesconto())
                .porcentagemDesconto(p.getPorcentagemDesconto())
                .custo(p.getCusto())
                .lucroEstimado(p.getLucroEstimado())
                .ativo(p.getAtivo())
                .destaque(p.getDestaque())
                .estoqueTotal(estoqueTotal)
                .estoqueMinimo(p.getEstoqueMinimo())
                .estoqueMaximo(p.getEstoqueMaximo())
                .localizacaoFisica(p.getLocalizacaoFisica())
                .codigoBarras(p.getCodigoBarras())
                .dimensoes(p.getDimensoes())
                .restricoes(new ArrayList<>(Optional.ofNullable(p.getRestricoes()).orElse(Collections.emptyList())))
                .tabelaNutricional(p.getTabelaNutricional())
                .modoDeUso(p.getModoDeUso())
                .palavrasChave(new ArrayList<>(Optional.ofNullable(p.getPalavrasChave()).orElse(Collections.emptyList())))
                .avaliacaoMedia(p.getAvaliacaoMedia())
                .comentarios(new ArrayList<>(Optional.ofNullable(p.getComentarios()).orElse(Collections.emptyList())))
                .dataCadastro(p.getDataCadastro())
                .dataUltimaAtualizacao(p.getDataUltimaAtualizacao())
                .dataValidade(p.getDataValidade())
                .fornecedorId(p.getFornecedorId())
                .cnpjFornecedor(p.getCnpjFornecedor())
                .contatoFornecedor(p.getContatoFornecedor())
                .prazoEntregaFornecedor(p.getPrazoEntregaFornecedor())
                .quantidadeVendida(p.getQuantidadeVendida())
                .vendasMensais(new ArrayList<>(Optional.ofNullable(p.getVendasMensais()).orElse(Collections.emptyList())))
                .imagemMimeType(p.getImagemMimeType())
                .imagemBase64(encodeBase64(p.getImagem()))
                .galeriaMimeTypes(new ArrayList<>(Optional.ofNullable(p.getGaleriaMimeTypes()).orElse(Collections.emptyList())))
                .galeriaBase64(encodeGaleria(p.getGaleria()))
                .build();
    }

    private static String encodeBase64(byte[] data) {
        return (data != null) ? Base64.getEncoder().encodeToString(data) : null;
    }

    private static List<String> encodeGaleria(List<byte[]> galeria) {
        if (galeria == null) return Collections.emptyList();
        List<String> base64List = new ArrayList<>();
        for (byte[] img : galeria) {
            base64List.add(encodeBase64(img));
        }
        return base64List;
    }
}
