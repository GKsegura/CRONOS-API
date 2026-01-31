package entities;

public enum Cliente {
    ALONSO_VERDIANI("Alonso e Verdiani"),
    COOCAFE("Coocafé"),
    COOPLUIZA("CoopLuiza"),
    COOPMETRO("Coopmetro"),
    CORURIPE("Coruripe"),
    CREDISIS("CrediSis"),
    LECOM("Lecom"),
    NEXUM("Nexum"),
    OMAR("Omar"),
    PARCEIRO("Parceiro"),
    SERVICOOP("Servicoop"),
    SICOOB_AGROCREDI("Sicoob Agrocredi"),
    SICOOB_AMAZONIA("Sicoob Amazonia"),
    SICOOB_CENTRAL_CECRESP("Sicoob Central Cecresp"),
    SICOOB_CENTRAL_SP("Sicoob Central SP"),
    SICOOB_CENTROSUL("Sicoob Centrosul"),
    SICOOB_COOCRELIVRE("Sicoob Coocrelivre"),
    SICOOB_COOPCRED("Sicoob Coopcred"),
    SICOOB_COOPLIVRE("Sicoob Cooplivre"),
    SICOOB_COOPMIL("Sicoob Coopmil"),
    SICOOB_CREDCEG("Sicoob Credceg"),
    SICOOB_CREDIACIL("Sicoob Crediacil"),
    SICOOB_CREDIACILPA("Sicoob Crediacilpa"),
    SICOOB_CREDIBRASILIA("Sicoob Credibrasilia"),
    SICOOB_CREDICAF("Sicoob Credicaf"),
    SICOOB_CREDICERIPA("Sicoob Crediceripa"),
    SICOOB_CREDICOM("Sicoob Credicom"),
    SICOOB_CREDICONSUMO("Sicoob Crediconsumo"),
    SICOOB_CREDICUCAR("Sicoob Crediçucar"),
    SICOOB_CREDIFIEMG("Sicoob Credifiemg"),
    SICOOB_CREDIFOR("Sicoob Credifor"),
    SICOOB_CREDIGUACU("Sicoob Crediguaçu"),
    SICOOB_CREDIMATA("Sicoob Credimata"),
    SICOOB_CREDIMINAS("Sicoob Crediminas"),
    SICOOB_CREDIMOGIANA("Sicoob Credimogiana"),
    SICOOB_CREDINOR("Sicoob Credinor"),
    SICOOB_CREDIPATOS("Sicoob Credipatos"),
    SICOOB_CREDIPONTAL("Sicoob Credipontal"),
    SICOOB_CREDIRIODOCE("Sicoob CrediRioDoce"),
    SICOOB_CREDIRURAL("Sicoob Credirural"),
    SICOOB_CREDISEGURO("Sicoob Crediseguro"),
    SICOOB_CREDISUDESTE("Sicoob Credisudeste"),
    SICOOB_CREDIVALE("Sicoob Credivale"),
    SICOOB_CREDJURD("Sicoob Credjurd"),
    SICOOB_CREDSAUDE("Sicoob Credsaude"),
    SICOOB_DIVICRED("Sicoob Divicred"),
    SICOOB_ENGECRED("Sicoob Engecred"),
    SICOOB_MANTIQUEIRA("Sicoob Mantiqueira"),
    SICOOB_NORTE_MT("Sicoob Norte MT"),
    SICOOB_NOSSACOOP("Sicoob Nossacoop"),
    SICOOB_NOSSO("Sicoob Nosso"),
    SICOOB_NOVA_CENTRAL("Sicoob Nova Central"),
    SICOOB_PAULISTA("Sicoob Paulista"),
    SICOOB_PRIMAVERA_MT("Sicoob Primavera MT"),
    SICOOB_PRO("Sicoob Pro"),
    SICOOB_SECOVICRED("Sicoob Secovicred"),
    SICOOB_SUDESTE_MAIS("Sicoob Sudeste Mais"),
    SICOOB_SUL("Sicoob Sul"),
    SICOOB_UNICENTRO_BR("Sicoob UniCentro BR"),
    SICOOB_UNIMAIS_CL_PAULISTA("Sicoob Unimais CL Paulista"),
    SOLAR("Solar");

    private final String nome;

    Cliente(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public static Cliente fromNome(String nome) {
        for (Cliente cliente : Cliente.values()) {
            if (cliente.getNome().equalsIgnoreCase(nome)) {
                return cliente;
            }
        }
        return null;
    }
}