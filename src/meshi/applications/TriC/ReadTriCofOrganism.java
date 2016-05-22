package meshi.applications.TriC;

import meshi.util.file.File2StringArray;

import java.util.StringTokenizer;

public class ReadTriCofOrganism {
	
	protected int indStart = Integer.MIN_VALUE;
	protected String organismName;
	protected String headerSeq;
	protected String ASeq;
	protected String BSeq;
	protected String GSeq;
	protected String DSeq;
	protected String HSeq;
	protected String QSeq;
	protected String ZSeq;
	protected String ESeq;
	protected String WSeq;
	
	public ReadTriCofOrganism(String filename) {
		String[] lines = File2StringArray.f2a(filename);
		organismName = lines[0];
		String orderString = lines[1].trim();
		String[] tmpStr = new String[9];
		for (int seq=0 ; seq<9 ; seq++) {
			tmpStr[seq] = "";
		}
		for (int c=3; c<lines.length ; c+=10) {
			for (int seq=0 ; seq<9 ; seq++) {
				StringTokenizer st = new StringTokenizer(lines[c+seq]);
				for ( ; st.countTokens()>2 ; st.nextToken()) {}
				tmpStr[seq] += st.nextToken();
			}
		}
		// Reading the header seq
		int indLetter = orderString.indexOf('0');
		headerSeq = tmpStr[indLetter];
		// Reading the other seqs
		indLetter = orderString.indexOf('A');
		ASeq = tmpStr[indLetter];
		indLetter = orderString.indexOf('B');
		BSeq = tmpStr[indLetter];
		indLetter = orderString.indexOf('G');
		GSeq = tmpStr[indLetter];
		indLetter = orderString.indexOf('D');
		DSeq = tmpStr[indLetter];
		indLetter = orderString.indexOf('H');
		HSeq = tmpStr[indLetter];
		indLetter = orderString.indexOf('Q');
		QSeq = tmpStr[indLetter];
		indLetter = orderString.indexOf('Z');
		ZSeq = tmpStr[indLetter];
		indLetter = orderString.indexOf('E');
		ESeq = tmpStr[indLetter];
	}

	/*
	 * A construcor for BOS
	 */
	public ReadTriCofOrganism() {
		headerSeq="-------------------MEGPLSVFG---DRSTGEAIRSQNVMAAASIANIVKSSLGPVGL"; 
		ASeq="-------------------MEGPLSVFG---DRSTGEAIRSQNVMAAASIANIVKSSLGPVGL"; 
		BSeq="---------------MASLSLAPVNIFKAGADEERAETARLSSFIGAIAIGDLVKSTLGPKGM"; 
		GSeq="--------------MMGHRPVLVLSQNT---KRESGRKVQSGNINAAKTIADIIRTCLGPKSM"; 
		DSeq="MPENVAPRTGPPAGAAGAAGGRGKSAYQ---DRDKPAQIRFSNISAAKAVADAIRTSLGPKGM"; 
		ESeq="------MASVGTLAFDEYGRPFLIIKDQDRKSRLMGLEALKSHIMAAKAVANTMKTSLGPNGL";
		HSeq="------------------MMPTPVILLKEGTDSSQGIPQLVSNISACQVIAEAVRTTLGPRGM"; 
		QSeq="--------MALHVPKAPGFAQMLKEGAK---HFSGLEEAVYRNIQACKELAQTTRTAYGPNGM"; 
		ZSeq="-----------------MAAVKTLNPKA---EVARAQAALAVNISAARGLQDVLRTNLGPKGT"; 
		WSeq="-----------------MAAIKAINSKA---EVARAQAALAVNICAARGLQDVLRTNLGPKGT"; 

		headerSeq+= "DKMLVDDIG--DVTITNDGATILKLLEVEHPAAKVLCELADLQDKEVGDGTTSVVIIAAE";    
		ASeq+= "DKMLVDDIG--DVTITNDGATILKLLEVEHPAAKVLCELADLQDKEVGDGTTSVVIIAAE";
		BSeq+= "DKILLSSGRDASLMVTNDGATILKNIGVDNPAAKVLVDMSRVQDDEVGDGTTSVTVLAAE";
		GSeq+= "MKMLLDPMG--GIVMTNDGNAILREIQVQHPAAKSMIEISRTQDEEVGDGTTSVIILAGE";
		DSeq+= "DKMIQDGKG--DVTITNDGATILKQMQVLHPAARMLVELSKAQDIEAGDGTTSVVIIAGS";
		ESeq+= "DKMMVDKDG--DVTVTNDGATILSMMDVDHQIAKLMVELSKSQDDEIGDGTTGVVVLAGA";
		HSeq+= "DKLIVDGRG--KATISNDGATILKLLDVVHPAAKTLVDIAKSQDAEVGDGTTSVTLLAAE";
		QSeq+= "NKMVINHLE--KLFVTNDAATILRELEVQHPAAKMIVMASHMQEQEVGDGTNFVLVFAGA";
		ZSeq+= "MKMLVSGAG--DIKLTKDGNVLLHEMQIQHPTASLIAKVATAQDDITGDGTTSNVLIIGE";
		WSeq+= "MKMLVSGAG--DVKLTKDGNVLLHEMQIQHPTASLIAKVATAQDDITGDGTTSNVLIIGE";

		headerSeq+= "LLKNADELVKQKIHPTSVISGYRLACKEAVRYISENLIINT---DELGRDCLINAAKTSM";    
		ASeq+= "LLKNADELVKQKIHPTSVISGYRLACKEAVRYISENLIINT---DELGRDCLINAAKTSM";
		BSeq+= "LLREAESLIAKKIHPQTIIAGWREATKAARQALLNSAVDHG-SDEVKFRQDLMNIAGTTL";
		GSeq+= "MLSVAEHFLEQQMHPTVVISAYRKALDDMISTLKKISIPVD----TSNRDTMLNIINSSI";
		DSeq+= "LLDSCTKLLQKGIHPTIISESFQKALEKGIEILTDMSRPEE----LSDRETLLNSAATSL";
		ESeq+= "LLEEAEQLLDRGIHPIRIADGYEQAARIAIEHLDKISDSVL--VDMKNTEPLIQTAKTTL";
		HSeq+= "FLKQVKPYVEEGLHPQIIIRAFRTATQLAVNKIKEIAVTVKKEDKVEQRKLLEKCAMTAL";
		QSeq+= "LLELAEELLRLGLSVSEVIEGYEIACKKAHEILPDLVCCSAKN--LRDVDEVSSLLHTSV";
		ZSeq+= "LLKQADLYISEGLHPRIITEGFEAAKEKALQFLEQVKVSKE-----MDRETLIDVARTSL";
		WSeq+= "LLKQADLYISEGLHPRIIAEGFEIAKIKALEVLEQVKIKKE-----MKREIHLDVARTSL";

		headerSeq+= "SSKVIGINGDFFANLVVDAVLAIKYTDIRGQPRYPVNSINVLKAHGRSQMESMLINGYAL";    
		ASeq+= "SSKVIGINGDFFANLVVDAVLAIKYTDIRGQPRYPVNSINVLKAHGRSQMESMLINGYAL";
		BSeq+= "SSKLLTHHKDHFTKLAVEAVLRLKG-------SGNLEAIHVIKKLGGSLADSYLDEGFLL";
		GSeq+= "TTKVISRWSSLACNIALDAVKTVQFEENGRKEIDIKKYARVEKIPGGIIEDSCVLRGVMI";
		DSeq+= "NSKVVSQYSSLLSPMSVDAVMKVIDPAT--ATSVDLRDIKIVKKLGGTIDDCELVEGLVL";
		ESeq+= "GSKVVNSCHRQMAEIAVNAVLTVADMQ---RRDVDFELIKVEGKVGGRLEDTKLIKGVIV";
		HSeq+= "SSKLISQQKAFFAKMVVDAVMMLDD-------LLQLKMIGIKKVQGGALEESQLVAGVAF";
		QSeq+= "MSKQYGNEVFLAKLIAQACVSIFPDS-----GHFNVDNIRVCKILGSGVHSSSVLHGMVF";
		ZSeq+= "RTKVHAELADVLTEAVVDSILAIKKQD----EPIDLFMVEIMEMKHKSETDTSLIRGLVL";
		WSeq+= "QTKVHPQLADVLTEAVVDSVLAIRRPN----YPIDLFMVEIMEMKHKSETDTKLIKGLVL";

		headerSeq+= "N---CVVGSQGMPKRIVNAKIACLDFSLQKTKMKLG-VQVVITDPEKLDQIRQRESDITK";    
		ASeq+= "N---CVVGSQGMPKRIVNAKIACLDFSLQKTKMKLG-VQVVITDPEKLDQIRQRESDITK";
		BSeq+= "D---KKIGVN-QPKRIENAKILIANTGMDTDKIKIFGSRVRVDSTAKVAEIEHAEKEKMK";
		GSeq+= "N---KDVTHPRMRRYIKNPRIVLLDSSLEYKKGESQ-TDIEITREEDFTRILQMEEEYIQ";
		DSeq+= "T---QKVANSGITR-VEKAKIGLIQFCLSAPKTDMD-NQIVVSDYVQMDRVLREERAYIL";
		ESeq+= "D---KDFSHPQMPKQVEDAKIAILTCPFEPPKPKTK-HKLDVTSVEDFKALQKYEKEKFE";
		HSeq+= "KKTFSYAGFEMQPKKYHNPMIALLNVELELKAEKDN-AEIRVHTVEDYQAIVDAEWNILY";
		QSeq+= "K-----KETEGDVTSVKDAKIAVYSCPFDGMITETK-GTVLIKSAEELMNFSKGEENLMD";
		ZSeq+= "D---HGARHPDMKKRVEDAYILTCNVSLEYEKTEVN-SGFFYKSAEEREKLVKAERKFIE";
		WSeq+= "D---HGARHPDMKKRVDDAFILTCNVSLEYEKTEVS-SGFFYKTAEEKEKLVKAERKFIE";

		headerSeq+= "ERIQKILATGANVILTTGGID---------DMCLKYFVEAGAMAVRRVLKRDLKRIAKAS";    
		ASeq+= "ERIQKILATGANVILTTGGID---------DMCLKYFVEAGAMAVRRVLKRDLKRIAKAS";
		BSeq+= "EKVERILKHGINCFINRQLIY---------NYPEQLFGAAGVMAIEHADFVGVERLALVT";
		GSeq+= "QLCEDIIQLKPDVVITEKGIS---------DLAQHYLMRANITAIRRVRKTDNNRIARAC";
		DSeq+= "NLVKQIKKTGCNVLLIQKSILRDA----LSDLALHFLNKMKIMVVKDIEREDIEFICKTI";
		ESeq+= "EMIRQIKETGANLAICQWGFD---------DEANHLLLQNDLPAVRWVGGPEIELIAIAT";
		HSeq+= "DKLEKIHHSGAKVVLSKLPIG---------DVATQYFADRDMFCAGRVPEEDLKRTMMAC";
		QSeq+= "AQVKAIADTGANVVVTGGRVA---------DMALHYANKYNIMLVRLNSKWDLRRLCKTV";
		ZSeq+= "DRVKKIIELKKKVCGDSDKGFVVINQKGIDPFSLDALAKEGIIALRRAKRRNMERLTLAC";
		WSeq+= "DRVQKIIDLKDKVCAQSNKGFVVINQKGIDPFSLDALAKHGILALRRAKRRNMERLSLAC";

		headerSeq+= "GATVLSTLANLEGEETFEASMLGQAEEVVQERICDDELILIKNTKAR-TSASVILRGAND";    
		ASeq+= "GATVLSTLANLEGEETFEASMLGQAEEVVQERICDDELILIKNTKAR-TSASVILRGAND";
		BSeq+= "GGEIASTFDHPE------LVKLGSCKLIEEVMIGEDKLIHFSGVALG-EACTIVLRGATQ";
		GSeq+= "GARIVSRPEELR-----EEDVGTGAGLLEIKKIGDEYFTFITECKDP-KACTILLRGASK";
		DSeq+= "GTKPVAHVDQFT-----ADMLGSAELAEEVSLNGSGKLIKITGCASPGKTVTIVVRGSNK";
		ESeq+= "GGRIVPRFSELT-----AEKLGFAGLVKEISFGTTKDKMLVIEQCKNSRAVTIFIRGGNK";
		HSeq+= "GGSIQTSVNALS------SDVLGRCQVFEETQIGGERYNFFTGCPKA-KTCTIILRGGAE";
		QSeq+= "GATALPRLNPPV------LEEMGHCDSVYLSEVGDTQVVVFKHEKEDGAISTIVLRGSTD";
		ZSeq+= "GGIALNSLDDLN------PDCLGHAGLVYEYTLGEEKFTFIEKCNNP-RSVTLLIKGPNK";
		WSeq+= "GGVAVNSVEDLS------VDCLGHAGLVYEYTLGEEKYTFIEDCINP-RSVTLLVKGPNK";

		headerSeq+= "FMCDEMERSLHDALCVVKRVLESKSVVPGGGAVEAALSIYLENYATSMGSREQLAIAEFA";    
		ASeq+= "FMCDEMERSLHDALCVVKRVLESKSVVPGGGAVEAALSIYLENYATSMGSREQLAIAEFA";
		BSeq+= "QILDEAERSLHDALCVLAQTVKDSRTVYGGGCSEMLMAHAVTQLASRTPGKEAVAMESYA";
		GSeq+= "EILSEVERNLQDAMQVCRNVLLDPQLVPGGGASEMAVAHALTEKSKAMTGVEQWPYRAVA";
		DSeq+= "LVIEEAERSIHDALCVIRCLVKKRALIAGGGAPEIELALRLTEYSRTLSGMESYCIRAFA";
		ESeq+= "MIIEEAKRSLHDALCVIRNLIRDNRVVYGGGAAEISCALAVSQEADKCPTLEQYAMRAFA";
		HSeq+= "QFMEETERSLHDAIMIVRRAIKNDSVVAGGGAIEMELSKYLRDYSRTIPGKQQLLIGAYA";
		QSeq+= "NLMDDIERAVDDGVNTFKVLTRDKRLVPGGGATEIELAKQITSYGETCPGLEQYAIKKFA";
		ZSeq+= "HTLTQIKDAIRDGLRAVKNAIDDGCVVPGAGAVEVAMAEALVKYKPSVKGRAQLGVQAFA";
		WSeq+= "HTLTQIKDAVRDGLRAIKNAIEDGCVVPGAGAVEVVIAEALVTYKHTIQGRARLGVQAFA";


		headerSeq+= "RSLPVIPNTLAVNAAQDSTDLVAKLRAFHNEAQVNPERKNLKWIGLDLV--NGKPRDNKQAG";    
		ASeq+= "RSLPVIPNTLAVNAAQDSTDLVAKLRAFHNEAQVNPERKNLKWIGLDLV--NGKPRDNKQAG";
		BSeq+= "KALRMLPTIIADNAGYDSADLVAQLRAAHSEG--------KTTAGLDMK--EGTIGDMSVLG";
		GSeq+= "QALEVIPRTLIQNCGASTIRLLTSLRAKHTQE-------NCETWGVNGE--TGTLVDMKELG";
		DSeq+= "DAMEVIPSTLAENAGLNPISTVTELRNRHAQG--------EKTTGINVR--KGGISNILEEQ";
		ESeq+= "DALEVIPMALAENSGMNPIQTMTEVRARQVKE-------VNPALGIDCL--HKGTNDMKHQH";
		HSeq+= "KALEIIPRQLCDNAGFDATNILNKLRARHAQG--------GMWYGVDIN--TEDIADNFEAF";
		QSeq+= "EAFEAIPRALAENSGVKANEVISKLYAVHQEG--------NKNVGLDIEAEVPAVKDMLEAG";
		ZSeq+= "DALLIIPKVLAQNSGFDLQETLVKVQAEHSES--------GQLVGVDLN--TGEPMVAAEAG";
		WSeq+= "DALLIIPKVLAQNSGYDLQETLVKVQAEHSNS--------KQPVGIDLN--TGEPMVAADAG";

		headerSeq+= "VFEPTIVKVKSLKFATEAAITILRIDDLIKLHPESKDD--KHGGYEDAVHSGALDA";    
		ASeq+= "VFEPTIVKVKSLKFATEAAITILRIDDLIKLHPESKDD--KHGGYEDAVHSGALDA";
		BSeq+= "ITESFQVKRQVLLSAAEAAEVILRVDNIIKAAPRKRVP--DHHPC-----------";
		GSeq+= "IWEPLAVKLQTYKTAVETAVLLLRIDDIVSGHKKKGDDQSRQGGAPDAGQE-----";
		DSeq+= "VVQPLLVSVSALTLATETVRSILKIDDVVNTR------------------------";
		ESeq+= "VIETLIGKKQQISLATQMVRMILKIDDIRKPGESEE--------------------";
		HSeq+= "VWEPAMVRINALTAASEAACLIVSVDETIKN-PRSTVD--ASPAAGRGRGRGRLH-";
		QSeq+= "VLDTYLGKYWAIKLATNAAVTVLRVDQIIMAKPAGGPK--PPSGKKDWDEDQND--";
		ZSeq+= "IWDNYCVKKQLLHSCTVIATNILLVDEIMRAGMSSLKG------------------";
		WSeq+= "VWDNYCVKKQLLHSCTVIATNILLVDEIMRAGMSSLKG------------------";	
	}
	
	public void setStartInd(int startInd) {
		indStart = startInd;
	}
	
	public char getLetter(char unit, int posInHeader) {
		int pos=indStart-1;
		int c=0;
		for ( ; (c<headerSeq.length()) && (pos<posInHeader) ; c++) {
			if (headerSeq.charAt(c)!='-')
				pos++;
		}
		if (c==headerSeq.length())
			throw new RuntimeException("\n\nAsked for position beyond the header sequence length\n\n");
		c--;
		char ch;
		switch(unit) {
		case 'A': ch = ASeq.charAt(c); break;
		case 'B': ch = BSeq.charAt(c); break;
		case 'G': ch = GSeq.charAt(c); break;
		case 'D': ch = DSeq.charAt(c); break;
		case 'H': ch = HSeq.charAt(c); break;
		case 'Q': ch = QSeq.charAt(c); break;
		case 'Z': ch = ZSeq.charAt(c); break;
		case 'E': ch = ESeq.charAt(c); break;
		default: throw new RuntimeException("\n\nInvalid Unit Type\n\n");
		}
		return ch;
	}
	
//	public void printAlign() {
//		System.out.println("\n"+organismName);
//		System.out.println("Head " + headerSeq.substring(0, 90));
//		System.out.println("SeqA " + ASeq.substring(0, 90));
//		System.out.println("SeqB " + BSeq.substring(0, 90));
//		System.out.println("SeqG " + GSeq.substring(0, 90));
//		System.out.println("SeqD " + DSeq.substring(0, 90));
//		System.out.println("SeqE " + ESeq.substring(0, 90));
//		System.out.println("SeqH " + HSeq.substring(0, 90));
//		System.out.println("SeqQ " + QSeq.substring(0, 90));
//		System.out.println("SeqZ " + ZSeq.substring(0, 90));
//	}
	
	public void printAlign() {
		System.out.println("\n"+organismName);
		System.out.println("Head " + headerSeq.substring(0));
		System.out.println("SeqA " + ASeq.substring(0));
		System.out.println("SeqB " + BSeq.substring(0));
		System.out.println("SeqG " + GSeq.substring(0));
		System.out.println("SeqD " + DSeq.substring(0));
		System.out.println("SeqE " + ESeq.substring(0));
		System.out.println("SeqH " + HSeq.substring(0));
		System.out.println("SeqQ " + QSeq.substring(0));
		System.out.println("SeqZ " + ZSeq.substring(0));
	}
}