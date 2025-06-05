# UniFit - Seu Aplicativo de Treinamento Personalizado

## Introdução

O UniFit é um aplicativo Android inovador projetado para revolucionar a maneira como alunos e instrutores interagem em academias e centros de treinamento. Desenvolvido com tecnologias modernas e foco na experiência do usuário, o UniFit oferece uma plataforma completa para gerenciamento de treinos, acompanhamento de progresso e comunicação eficiente. Este projeto representa o esforço colaborativo de uma equipe dedicada a criar uma solução robusta e intuitiva para o universo fitness, facilitando a jornada de alunos em busca de seus objetivos e otimizando o trabalho dos profissionais da área.

'''



## Tecnologias Utilizadas

O desenvolvimento do UniFit foi pautado na utilização de tecnologias modernas e eficientes para a plataforma Android. A linguagem de programação principal é o **Kotlin**, escolhida por sua concisão, segurança e interoperabilidade com Java. O projeto utiliza o **Android SDK** mais recente (compileSdk 35, targetSdk 35) para garantir compatibilidade e acesso aos recursos mais atuais da plataforma.

A arquitetura do aplicativo se apoia fortemente no **Firebase**, a plataforma de desenvolvimento de aplicativos do Google. Utilizamos o **Firebase Authentication** para gerenciar o login e cadastro de usuários de forma segura, incluindo verificação de e-mail e recuperação de senha. O **Firebase Firestore** atua como nosso banco de dados NoSQL principal, armazenando dados de usuários, treinos, exercícios e outras informações relevantes de forma escalável e em tempo real. O **Firebase Storage** é empregado para o armazenamento de imagens de perfil dos usuários. Além disso, o **Firebase Analytics** pode ser configurado para coletar dados de uso e entender o comportamento dos usuários dentro do app.

Para a interface do usuário, adotamos os princípios do **Material Design**, utilizando a biblioteca **Material Components for Android**. Isso garante uma experiência visual consistente, moderna e intuitiva, com componentes como `BottomNavigationView`, `CardView`, `RecyclerView` e outros elementos visuais padronizados. A biblioteca **Glide** foi integrada para o carregamento e cache eficientes de imagens, especialmente as fotos de perfil dos usuários e imagens de exercícios. A **CircleImageView** é utilizada para exibir avatares de forma arredondada, um padrão visual comum em aplicativos sociais e de perfil.

O projeto é gerenciado com o **Gradle**, utilizando a sintaxe Kotlin (build.gradle.kts) para a configuração das dependências e do processo de build. As dependências incluem bibliotecas do **AndroidX** (Core KTX, AppCompat, Activity, ConstraintLayout) que formam a base do desenvolvimento Android moderno. Para testes, o projeto está configurado para usar **JUnit** e **Espresso**.





## Funcionalidades Principais

O UniFit foi concebido para oferecer uma experiência completa tanto para alunos quanto para administradores (instrutores ou funcionários da academia). As funcionalidades foram cuidadosamente implementadas para cobrir os principais aspectos da gestão de treinos e interação.

**Para Alunos:**

*   **Autenticação Segura:** O processo de login e cadastro é gerenciado pelo Firebase Authentication, garantindo segurança e praticidade. Os alunos podem se cadastrar com e-mail e senha, recuperar senhas esquecidas e precisam verificar seus e-mails para ativar a conta, adicionando uma camada extra de segurança.
*   **Home Personalizada:** Ao fazer login, o aluno é direcionado para uma tela inicial que exibe seu nome e foto de perfil (carregada via Glide a partir do Firestore/Storage). A home apresenta os treinos disponíveis (geralmente A, B e C), permitindo acesso rápido aos exercícios do dia.
*   **Visualização de Treinos:** O aluno pode selecionar um dos treinos (A, B ou C) para visualizar a lista de exercícios prescritos, incluindo nome e número de repetições/séries. Essa funcionalidade busca os dados diretamente do Firestore, garantindo que o treino esteja sempre atualizado.
*   **Gerenciamento de Perfil:** Através da tela de perfil, o aluno pode visualizar e editar suas informações pessoais, incluindo a foto de perfil, que é armazenada de forma eficiente.
*   **Navegação Intuitiva:** A navegação principal do aplicativo para alunos é feita através de uma `BottomNavigationView`, permitindo alternar facilmente entre as seções principais como Home, Calendário, Chat (se implementado) e Contato.
*   **Interação e Notificações:** O aplicativo inclui funcionalidades para interação, como um possível chat e um sistema de notificações para manter o aluno informado sobre atualizações ou mensagens importantes.

**Para Administradores/Funcionários:**

*   **Login Diferenciado:** O sistema identifica usuários com permissões de administrador durante o login e os direciona para uma interface específica.
*   **Gerenciamento de Alunos:** Administradores têm acesso a uma lista completa de alunos cadastrados, exibida em um `RecyclerView` com nome e foto. Eles podem selecionar um aluno específico para visualizar detalhes ou gerenciar seus treinos.
*   **Edição de Treinos:** Uma funcionalidade crucial é a capacidade de editar os treinos dos alunos. Os administradores podem adicionar, remover ou modificar exercícios dentro das rotinas (A, B, C) de cada aluno, personalizando o treinamento.
*   **Cadastro de Exercícios:** Administradores podem gerenciar a base de exercícios disponíveis no aplicativo, adicionando novos exercícios que poderão ser incluídos nos treinos dos alunos.
*   **Comunicação e Suporte:** Ferramentas como chat ou um sistema de notificações dedicado aos administradores facilitam a comunicação com os alunos e o gerenciamento de avisos.
*   **Navegação Administrativa:** Assim como os alunos, os administradores possuem uma navegação otimizada, geralmente via `BottomNavigationView`, para acessar as diferentes áreas de gerenciamento.

O fluxo de dados é gerenciado primariamente pelo Firestore, garantindo que as informações estejam sincronizadas em tempo real entre as diferentes partes do aplicativo e os diferentes tipos de usuário. A estrutura inicial de treinos (A, B, C com exercícios básicos) é criada automaticamente para novos usuários, facilitando o início da utilização do app.





## Instalação e Configuração

Para executar o projeto UniFit em seu ambiente de desenvolvimento, siga estas etapas essenciais. Primeiramente, é necessário ter o Android Studio instalado e configurado corretamente em sua máquina. Clone este repositório para o seu ambiente local utilizando o Git.

```bash
git clone <URL_DO_REPOSITORIO>
cd UniFit-master
```

Após clonar o projeto, abra-o no Android Studio. O UniFit depende fortemente dos serviços do Firebase. Portanto, você precisará criar um projeto no [Firebase Console](https://console.firebase.google.com/) e registrar um aplicativo Android com o nome de pacote `com.example.atividade1` (ou o nome de pacote configurado no `app/build.gradle.kts`). Siga as instruções do Firebase para adicionar os serviços necessários, como Authentication, Firestore e Storage. Baixe o arquivo de configuração `google-services.json` fornecido pelo Firebase e coloque-o no diretório `app/` do projeto.

Com o Firebase configurado, o Android Studio deverá sincronizar o projeto e baixar todas as dependências listadas nos arquivos `build.gradle.kts`. Certifique-se de que todas as dependências do Gradle foram resolvidas com sucesso. Conecte um dispositivo Android ou inicie um emulador e, em seguida, compile e execute o aplicativo através do Android Studio. O aplicativo deverá iniciar, permitindo que você se cadastre ou faça login para testar as funcionalidades.

## Estrutura do Código

A organização do código-fonte do UniFit busca seguir boas práticas de desenvolvimento Android, separando responsabilidades e facilitando a manutenção. O código principal reside em `/app/src/main/java/com/example/atividade1/`. As Activities (telas) são nomeadas de forma descritiva, indicando seu propósito e, em alguns casos, o tipo de usuário (prefixos `aluno_` e `func_`). Por exemplo, `login.kt` gerencia a tela de login, `aluno_Home.kt` a tela inicial do aluno, e `func_EditaAlunos.kt` a tela de gerenciamento de alunos para administradores.

Modelos de dados, como `Aluno.kt`, `Exercise.kt` e `Notificacao.kt`, representam as entidades principais do sistema e são utilizados para interagir com o Firestore. Adapters, como `AlunoAdapter.kt` e `ExerciciosAdapter.kt`, são responsáveis por conectar os dados (geralmente listas obtidas do Firestore) aos componentes de UI como `RecyclerView`, permitindo a exibição eficiente de listas dinâmicas.

Classes auxiliares, como `aluno_PopMenuHelper.kt`, `func_PopupMenuHelper.kt`, `setupBottomNavigation.kt` e `TopBarHelper.kt`, encapsulam lógicas reutilizáveis, como a criação de menus popup e a configuração da barra de navegação inferior, promovendo a reutilização de código e a separação de conceitos. A interação com o Firebase é distribuída pelas Activities e, potencialmente, por classes de serviço ou repositórios dedicados (embora a estrutura exata possa variar).

Os recursos da interface do usuário, como layouts XML, strings, cores e drawables, estão localizados no diretório `/app/src/main/res/`. Os layouts definem a estrutura visual de cada tela, enquanto os outros diretórios contêm os recursos visuais e textuais utilizados em todo o aplicativo, seguindo a estrutura padrão de projetos Android.

## Autores

Este projeto é o resultado do trabalho colaborativo e dedicado dos seguintes integrantes:

*   Tiago Paik Monteiro
*   Savas Constantin Petalas Neto
*   Levi de Pontes Lima Santos
*   Paulo Shizuo Vasconcelos Tatibana
*   Jonh Henrique Moreira da Costa

Cada membro contribuiu significativamente para a concepção, desenvolvimento e implementação das diversas funcionalidades que compõem o UniFit.

## Agradecimentos

Agradecemos a todos que apoiaram o desenvolvimento deste projeto, seja através de orientação, feedback ou testes. O UniFit representa um passo importante na aplicação de tecnologia para melhorar a experiência no ambiente fitness.


