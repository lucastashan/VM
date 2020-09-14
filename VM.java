// PUCRS, Escola Politécnica, Engenharia de Software
// Disciplina Sistemas Operacionais
// Alunos: Lucas Tashan, ??? e Nikolas Lacerda
// Trabalho - Parte I
// A VM completa, construida pelo professor, incluindo o programa P1, tem 234 linhas.

public class VM {

	private final GM gm = new GM();

	private enum Opcode {
		DADO, ___,		    // se memoria nesta posicao tem um dado, usa DADO, se nao usada ee NULO
		JMP, JMPI, JMPIG, JMPIL, JMPIE, ADDI, SUBI, ANDI, ORI, LDI, LDD, STD, ADD, SUB, MULT, LDX, STX, SWAP, STOP, JMPIM, JMPIGM, JMPILM, JMPIEM;
	}

	private class Word { 	// cada posicao da memoria ee uma plavra, e tem uma instrucao (ou um dado)
		public Opcode opc; 	// opcode
		public int r1; // indice do primeiro registrador da operacao (Rs ou Rd cfe opcode na tabela)
		public int r2; // indice do segundo registrador da operacao (Rc ou Rs cfe operacao)
		public int p;  // parametro para instrucao (k ou A cfe operacao), ou o dado, se opcode = DADO

		public Word(final Opcode _opc, final int _r1, final int _r2, final int _p) {  
			opc = _opc;   r1 = _r1;    r2 = _r2;	p = _p;
		}
	}

	private enum Interrupts {  // possiveis interrupcoes
		noInterrupt, intEnderecoInvalido, intInstrucaoInvalida, intSTOP;
	}

	private class CPU {
		private int pc; 			// ... composto de program counter,
		private Word ir; 			// instruction register,
		private final int[] reg;       	// registradores da CPU
		private Interrupts irpt; 	// durante instrucao, interrupcao pode ser sinalizada
		private int base;   		// base e limite de acesso na memoria
		private int limite; // por enquanto toda memoria pode ser acessada pelo processo rodando
							// ATE AQUI: contexto da CPU - tudo que precisa sobre o estado de um processo
							// para executar
							// nas proximas versoes isto pode modificar, e vai permitir salvar e restaurar
							// um processo na CPU

		private final Word[] m;   // CPU acessa MEMORIA, guarda referencia 'm' a ela. memoria nao muda. ee sempre a mesma.
						
		public CPU(final Word[] _m) {     // ref a MEMORIA passada na criacao da CPU
			m = _m; 				// usa o atributo 'm' para acessar a memoria.
			reg = new int[8]; 		// aloca o espaço dos registradores
		}

		public void setContext(final int _base, final int _limite, final int _pc) {  // no futuro esta funcao vai ter que ser 
			base = _base;                                          //expandida para setar TODO contexto de execucao,
			limite = _limite;									   // agora,  setamos somente os registradores base,
			pc = _pc;                                              // limite e pc (deve ser zero nesta versao)
			irpt = Interrupts.noInterrupt;                         // reset da interrupcao registrada
		}

		private boolean legal(final int e) {                             // todo acesso a memoria tem que ser verificado
			if ((e < base) || (e > limite)) {  
			System.out.println("PC ->" + e);                 //  valida se endereco 'e' na memoria ee posicao legal
				irpt = Interrupts.intEnderecoInvalido;             //  caso contrario ja liga interrupcao
				return false;
			};
			return true;
		}

		public void run() { 		// execucao da CPU supoe que o contexto da CPU, vide acima, esta devidamente setado
			while (true) { 			// ciclo de instrucoes. acaba cfe instrucao, veja cada caso.
				// FETCH
				//System.out.println(pc);
				//System.out.println("POSICAO ATUAL: " + reg[0]);
			//	System.out.println("SWAPS: " + reg[1]);
				for (int j = 0; j < reg.length; j++) {
					//System.out.print(" REG " + (j) + " = " + reg[j]);
				}
				//System.out.println("");
				//System.out.print("["); 
				for (int j = 55; j < m.length; j++) {
					if(j > 69){
						break;
					}
					//System.out.print(m[j].p + ",");
				}
				//System.out.print("]");
					
				if (legal(pc)) { 	// pc valido
					ir = m[pc]; 	// busca posicao da memoria apontada por pc, guarda em ir
					//System.out.println(" ... Instrução atual:" + ir.opc + " " + ir.r1 + " " + ir.r2 + " " + ir.p);
				// EXECUTA INSTRUCAO NO ir
					switch (ir.opc) { // DADO,JMP,JMPI,JMPIG,JMPIL,JMPIE,JMPIGM,JMPILM,JMPIEM,ADDI,SUBI,LDI,LDD,STD,ADD,SUB,MULT,LDX,STX,SWAP,STOP;
					
						case LDI: // Rd ← k
							reg[ir.r1] = ir.p; // registrador destino recebe o dado
							pc++; // pc++
							break;

						case STD: // [A] ← Rs
						    if (legal(ir.p)) {
							    m[ir.p].opc = Opcode.DADO; // posicao da memoria salva como dado
							    m[ir.p].p = reg[ir.r1]; // posicao da memoria recebe o dado
							    pc++;
								};
							break;

						case ADD: // Rd ← Rd + Rs
							reg[ir.r1] = reg[ir.r1] + reg[ir.r2];
							pc++;
							break;

						case ADDI: // Rd ← Rd + k
							reg[ir.r1] = reg[ir.r1] + ir.p;
							pc++;
							break;

						case LDD: // Rd ← [A]
							reg[ir.r1] = m[ir.p].p;
							pc++; // pc++
						break;

						case LDX: // Rd ← [Rs]
							reg[ir.r1] = m[reg[ir.r2]].p; 
							pc++; // pc++
						break;
						
						case STX: // [Rd] ← Rs
							if (legal(reg[ir.r1])) {
								m[reg[ir.r1]].opc = Opcode.DADO;
								m[reg[ir.r1]].p = reg[ir.r2]; 
								pc++;
							};  
						break;

						case SUB: // Rd ← Rd - Rs
							reg[ir.r1] = reg[ir.r1] - reg[ir.r2];
							pc++;
						break;

						case SUBI: // Rd ← Rd – k
							reg[ir.r1] = reg[ir.r1] - ir.p;
							pc++;
						break;

						case JMP: // PC ← k
							if (legal(ir.p)) {
								pc = ir.p;
							}
						break;

						case JMPI: //PC ← Rs
							if (legal(reg[ir.p])) {
								pc = reg[ir.p];
							}
						break;

						case JMPIG: // If Rc > 0 Then PC ← Rs Else PC ← PC +1
							 if(reg[ir.r2] > 0){
								pc = reg[ir.r1];
							 }else{
								 pc++;
							 }
						break;

						case JMPIL: // If Rc < 0 Then PC ← Rs Else PC ← PC +1
							 if(reg[ir.r2] < 0){
								pc = reg[ir.r1];
							 }else{
								 pc++;
							 }
						break;

						case JMPIE: // If Rc = 0 Then PC ← Rs Else PC ← PC +1
							 if(reg[ir.r2] == 0){
								pc = reg[ir.r1];
							 }else{
								 pc++;
							 }
						break;

						case JMPIM: // PC ← [A]
							if (legal(reg[ir.p])) {
								 pc = m[ir.p].p;
							}
						break;

						case JMPIGM: // If Rc > 0 Then PC ← [A] Else PC ← PC +1
							 if(reg[ir.r2] > 0){
								pc = m[ir.p].p;
							 }else{
								 pc++;
							 }
						break;

						case JMPILM: // If Rc < 0 Then PC ← [A] Else PC ← PC +1
							 if(reg[ir.r2] < 0){
								pc = m[ir.p].p;
							 }else{
								 pc++;
							 }
						break;

						case JMPIEM: // If Rc = 0 Then PC ← [A] Else PC ← PC +1
							 if(reg[ir.r2] == 0){
								pc = m[ir.p].p;
							 }else{
								 pc++;
							 }
						break;

						case MULT: // Rd ← Rd * Rs
							reg[ir.r1] = reg[ir.r1] * reg[ir.r2];
							pc++;
						break;

						case SWAP: // T ← Ra, Ra ← Rb, Rb ←T
							final int temp = reg[ir.r1];
							reg[ir.r1] = reg[ir.r2];
							reg[ir.r2] = temp;
							pc++;
						break;

						case STOP: //  para execucao
							irpt = Interrupts.intSTOP;
              break;
                            
						case DADO:
							pc++;
							break;

						default:
							 
							break;
					}
				}
				// verifica int - agora simplesmente para programa em qualquer caso
				if (!(irpt == Interrupts.noInterrupt)) {
					System.out.print("Interrupcao ");
					System.out.println(irpt);
					break; // break sai do loop da cpu
				}
			}
		}
	}

	// -------------------------------------------  classes e funcoes auxiliares
	private class Aux {
		public void dump(final Word w) {
			System.out.print("[ "); 
			System.out.print(w.opc); System.out.print(", ");
			System.out.print(w.r1);  System.out.print(", ");
			System.out.print(w.r2);  System.out.print(", ");
			System.out.print(w.p);   System.out.println("  ] ");
		}
		public void dump(final Word[] m, final int ini, final int fim) {
			for (int i = ini; i < fim; i++) {
				System.out.print(i); System.out.print(":  ");  dump(m[i]);
			}
		}
		public void carga(final Word[] p, final Word[] m) {
			final int[] pages = gm.aloca(p.length);
			for(final int j = 0; j < pages.length; i++){
				for (int i = gm.pageStart(pages[j]); i < gm.pageEnd(pages[j]); i++) {
					m[i].opc = p[i].opc;     m[i].r1 = p[i].r1;     m[i].r2 = p[i].r2;     m[i].p = p[i].p;
				}
			}
		}
	}
	// -------------------------------------------  fim classes e funcoes auxiliares

	// -------------------------------------------- atributos e construcao da VM
	public int tamMem;    
    public Word[] m;     
    public CPU cpu;    
    public Aux aux;

    public VM(){
		tamMem = 1024;
		m = new Word[tamMem]; // m ee a memoria
		for (int i=0; i<tamMem; i++) { m[i] = new Word(Opcode.___,-1,-1,-1); };
		cpu = new CPU(m);
		aux = new Aux();
	}	

	// -------------------------------------------- teste da VM ,  veja classe de programas
	public void test1(){
		final Word[] p1 = new Programas().p1;
		aux.carga(p1, m);
		final Word[] p2 = new Programas().p2;
		aux.carga(p2, m);
		final Word[] p3 = new Programas().p3;
		aux.carga(p3, m);
		cpu.setContext(0, tamMem - 1, 0);
		System.out.println("---------------------------------- programa carregado ");
		aux.dump(m, 0, 200);
		//System.out.println("---------------------------------- após execucao ");
		//cpu.run();
		//aux.dump(m, 0, 70);
	} 

   // ---------------------------------------------- instancia e testa VM
	public static void main(final String args[]) {
		final VM vm = new VM();
		vm.test1();
	}

   //  -------------------------------------------- programas aa disposicao para copiar na memoria (vide aux.carga)
   private class Programas {
	   public Word[] p1 = new Word[] {
				new Word(Opcode.LDI, 0, -1, 0),
				new Word(Opcode.STD, 0, -1, 50),
				new Word(Opcode.LDI, 1, -1, 1),
				new Word(Opcode.STD, 1, -1, 51),
				new Word(Opcode.LDI, 7, -1, 52),
				new Word(Opcode.LDI, 5, -1, 6),
				new Word(Opcode.LDI, 6, -1, 61),
				new Word(Opcode.LDI, 2, -1, 0),
				new Word(Opcode.ADD, 2, 0, -1),
				new Word(Opcode.LDI, 0, -1, 0),
				new Word(Opcode.ADD, 0, 1, -1),
				new Word(Opcode.ADD, 1, 2, -1),
				new Word(Opcode.STX, 7, 1, -1),
				new Word(Opcode.ADDI, 7, -1, 1),
				new Word(Opcode.SUB, 6, 7, -1),
				new Word(Opcode.JMPIG, 5, 6, -1),
				new Word(Opcode.STOP, -1, -1, -1),
			};
			int valor = 20;
			public Word[] p2 = new Word[] {
				new Word(Opcode.LDI, 0, -1, valor), //0
				new Word(Opcode.STD, 0, -1, 48), // 1
				new Word(Opcode.LDD, 0, -1, 48), // 2 - le um valor de uma determinada posição (carregada no inicio)
				new Word(Opcode.LDI, 1, -1, 6), //3
				new Word(Opcode.JMPIG, 1, 0, -1), // 4  
				//se o número for menor que zero coloca -1 no início da posição de memória para saída
				new Word(Opcode.STOP, -1, -1, -1), // 5
				//se for > que 0 este é o num de valores da sequencia de fibonacci a serem escritos em sequencia a partir de uma posição de memoria
				//loop
				new Word(Opcode.LDI, 0, -1, 0), //6
				new Word(Opcode.STD, 0, -1, 50), //7
				new Word(Opcode.LDI, 1, -1, 1), //8
				new Word(Opcode.STD, 1, -1, 51), //9
				new Word(Opcode.LDI, 5, -1, 12), //10
				new Word(Opcode.LDI, 3, -1, 52), //11 // pula pra 12
				new Word(Opcode.LDI, 6, -1, 0), //12
				new Word(Opcode.ADDI, 6, -1, 50), //12
				new Word(Opcode.ADDI, 6, -1, valor), //13
				new Word(Opcode.LDI, 2, -1, 0), //14
				new Word(Opcode.ADD, 2, 0, -1), //15
				new Word(Opcode.LDI, 0, -1, 0),  //16
				new Word(Opcode.ADD, 0, 1, -1), //17
				new Word(Opcode.ADD, 1, 2, -1), //18
				new Word(Opcode.STX, 3, 1, -1),//19
				// Controla loop
				new Word(Opcode.ADDI, 3, -1, 1), //20
				new Word(Opcode.SUB, 6, 3, -1), // 21
				//new Word(Opcode.SUBI, 6, -1, 1), // 22
				new Word(Opcode.JMPIG, 5, 6, -1), // 23 VERIFICA LOOP
				new Word(Opcode.STOP, -1, -1, -1) //24
			};
			int valor2 = -2;
			public Word[] p3 = new Word[] {
				new Word(Opcode.LDI, 0, -1, valor2), //0
				new Word(Opcode.STD, 0, -1, 49), // 1
				new Word(Opcode.LDD, 0, -1, 49), // load do valor no r0
				new Word(Opcode.LDI, 2, -1, 12), // posição pra pular caso não for < 0
				new Word(Opcode.JMPIL, 2, 0, -1), // pula pra 12 se for < 0
				new Word(Opcode.LDI, 3, -1, 11), // posição pra quando o prog acabar
				new Word(Opcode.ADD, 1, 0, -1), // load do valor no r1
				new Word(Opcode.SUBI, 1, -1, 1), //r1 - 1 (valor anterior)
				new Word(Opcode.JMPIE, 3, 1, -1), // pula pra fim se chegou em 0
				new Word(Opcode.MULT, 0, 1, -1), // multiplica
				new Word(Opcode.JMP, -1, -1, 7), // volta pra linha 7 para calcular o prox
				new Word(Opcode.STD, 0, -1, 50), // chegou ao fim e salvou o fat na pos 50
				new Word(Opcode.STOP, -1, -1, -1), // é menor que 0 e escreveu -1
			};
			int valor3 = 5;
			int valor4 = valor3 - 1; // como ele pega de 2 em 2
			public Word[] p4 = new Word[] {
				new Word(Opcode.LDI, 0, -1, valor3), //0
				new Word(Opcode.STD, 0, -1, 55), // 1
				new Word(Opcode.LDI, 0, -1, 10), //2
				new Word(Opcode.LDI, 1, -1, 5), //3
				new Word(Opcode.LDI, 2, -1, 9), //4
				new Word(Opcode.LDI, 3, -1, 8), //5
				new Word(Opcode.LDI, 4, -1, 6), //6
				new Word(Opcode.LDI, 5, -1, 7), //7
				new Word(Opcode.LDI, 6, -1, 1), //8
				new Word(Opcode.LDI, 7, -1, 2), //9
				new Word(Opcode.STD, 0, -1, 56), // 10
				new Word(Opcode.STD, 1, -1, 57), // 11
				new Word(Opcode.STD, 2, -1, 58), // 12
				new Word(Opcode.STD, 3, -1, 59), // 13
				new Word(Opcode.STD, 4, -1, 60), // 14
				new Word(Opcode.STD, 5, -1, 61), // 15
				new Word(Opcode.STD, 6, -1, 62), // 16
				new Word(Opcode.STD, 7, -1, 63), // 17
				new Word(Opcode.LDI, 0, -1, 4), //18
				new Word(Opcode.LDI, 1, -1, 3), //19
				new Word(Opcode.STD, 0, -1, 64), // 20
				new Word(Opcode.STD, 1, -1, 65), // 21
			
				// INICIA
				new Word(Opcode.LDI, 2, -1, 56), // r3 = 50 // 22
				new Word(Opcode.ADDI, 2, -1, valor4), // r3 = 55 ( limite ) // 23
				new Word(Opcode.LDI, 0, -1, 56), // posicao atual = r1 // 24
				new Word(Opcode.LDI, 1, -1, 0), // swap == 0 -> r2 // 25
				
				new Word(Opcode.SUB, 7, 7, -1), // 26
				new Word(Opcode.ADD, 7, 0, -1), // 27
				new Word(Opcode.SUB, 7, 2, -1), // 50 - 55 = -5 //28
				new Word(Opcode.LDI, 3, -1, 47), // posicao se acabou //29
				new Word(Opcode.JMPIE, 3, 7, -1), // pula se chegou no fim //30
				// nao chegou no fim
				new Word(Opcode.LDX, 4, 0, -1), //31
				new Word(Opcode.ADDI, 0, -1, 1), //32
				new Word(Opcode.LDX, 5, 0, -1), //33
				new Word(Opcode.LDI, 6, -1, 40), // se der swap //34
				new Word(Opcode.SUB, 7, 7, -1), // 35
				new Word(Opcode.ADD, 7, 4, -1), // 36
				new Word(Opcode.SUB, 7, 5, -1), // 37
				new Word(Opcode.JMPIG, 6, 7, -1), // 38 // vai pro swap
				//new Word(Opcode.ADDI, 0, -1, 1), //pos++ /
				new Word(Opcode.JMP, -1, -1, 26), // volta // 39
				// deu swap
				new Word(Opcode.STX, 0, 4, -1), //40
				new Word(Opcode.SUB, 7, 7, -1), // 41
				new Word(Opcode.ADD, 7, 0, -1), // 42
				new Word(Opcode.SUBI, 7, -1, 1), // 43
				new Word(Opcode.STX, 7, 5, -1), // 44
				new Word(Opcode.ADDI, 1, -1, 1), //45
				new Word(Opcode.JMP, -1, -1, 26), // 46
				new Word(Opcode.LDI, 3, -1, 52), // 47
				new Word(Opcode.JMPIE, 3, 1, -1), // 48
				new Word(Opcode.LDI, 0, -1, 56), // 49
				new Word(Opcode.LDI, 1, -1, 0), // 50
				new Word(Opcode.JMP, -1, -1, 26), // 51
				new Word(Opcode.STOP, -1, -1, -1), // 52

		};
	}
}
