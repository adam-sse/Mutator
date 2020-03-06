// Generated from SimpleC.g4 by ANTLR 4.8
package parsing.grammar;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SimpleCParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		BASIC_TYPE=39, LITERAL=40, IDENTIFIER=41, WHITESPACE=42, LINE_COMMENT=43, 
		BLOCK_COMMENT=44;
	public static final int
		RULE_file = 0, RULE_function = 1, RULE_declList = 2, RULE_declaration = 3, 
		RULE_declType = 4, RULE_stmtCompound = 5, RULE_stmt = 6, RULE_stmtEmpty = 7, 
		RULE_stmtDeclaration = 8, RULE_stmtAssignment = 9, RULE_stmtReturn = 10, 
		RULE_stmtExpr = 11, RULE_stmtBranch = 12, RULE_stmtLoop = 13, RULE_stmtWhileLoop = 14, 
		RULE_stmtDoWhileLoop = 15, RULE_expr = 16, RULE_exprList = 17;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "function", "declList", "declaration", "declType", "stmtCompound", 
			"stmt", "stmtEmpty", "stmtDeclaration", "stmtAssignment", "stmtReturn", 
			"stmtExpr", "stmtBranch", "stmtLoop", "stmtWhileLoop", "stmtDoWhileLoop", 
			"expr", "exprList"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "','", "'unsigned'", "'signed'", "'*'", "'{'", "'}'", 
			"';'", "'='", "'return'", "'if'", "'else'", "'while'", "'do'", "'['", 
			"']'", "'++'", "'--'", "'-'", "'~'", "'!'", "'/'", "'%'", "'+'", "'<<'", 
			"'>>'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&'", "'^'", "'|'", 
			"'&&'", "'||'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, "BASIC_TYPE", "LITERAL", "IDENTIFIER", "WHITESPACE", 
			"LINE_COMMENT", "BLOCK_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SimpleC.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SimpleCParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class FileContext extends ParserRuleContext {
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << BASIC_TYPE))) != 0)) {
				{
				{
				setState(36);
				function();
				}
				}
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionContext extends ParserRuleContext {
		public DeclTypeContext type;
		public Token name;
		public DeclListContext params;
		public StmtCompoundContext body;
		public DeclTypeContext declType() {
			return getRuleContext(DeclTypeContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(SimpleCParser.IDENTIFIER, 0); }
		public DeclListContext declList() {
			return getRuleContext(DeclListContext.class,0);
		}
		public StmtCompoundContext stmtCompound() {
			return getRuleContext(StmtCompoundContext.class,0);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			((FunctionContext)_localctx).type = declType();
			setState(43);
			((FunctionContext)_localctx).name = match(IDENTIFIER);
			setState(44);
			match(T__0);
			setState(45);
			((FunctionContext)_localctx).params = declList();
			setState(46);
			match(T__1);
			setState(47);
			((FunctionContext)_localctx).body = stmtCompound();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclListContext extends ParserRuleContext {
		public List<DeclarationContext> declaration() {
			return getRuleContexts(DeclarationContext.class);
		}
		public DeclarationContext declaration(int i) {
			return getRuleContext(DeclarationContext.class,i);
		}
		public DeclListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declList; }
	}

	public final DeclListContext declList() throws RecognitionException {
		DeclListContext _localctx = new DeclListContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << BASIC_TYPE))) != 0)) {
				{
				setState(49);
				declaration();
				}
			}

			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(52);
				match(T__2);
				setState(53);
				declaration();
				}
				}
				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclarationContext extends ParserRuleContext {
		public Token name;
		public DeclTypeContext declType() {
			return getRuleContext(DeclTypeContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(SimpleCParser.IDENTIFIER, 0); }
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			declType();
			setState(60);
			((DeclarationContext)_localctx).name = match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclTypeContext extends ParserRuleContext {
		public Token modifier;
		public Token type;
		public Token ptr;
		public TerminalNode BASIC_TYPE() { return getToken(SimpleCParser.BASIC_TYPE, 0); }
		public DeclTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declType; }
	}

	public final DeclTypeContext declType() throws RecognitionException {
		DeclTypeContext _localctx = new DeclTypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_declType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3 || _la==T__4) {
				{
				setState(62);
				((DeclTypeContext)_localctx).modifier = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__3 || _la==T__4) ) {
					((DeclTypeContext)_localctx).modifier = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(65);
			((DeclTypeContext)_localctx).type = match(BASIC_TYPE);
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(66);
				((DeclTypeContext)_localctx).ptr = match(T__5);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtCompoundContext extends ParserRuleContext {
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public StmtCompoundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtCompound; }
	}

	public final StmtCompoundContext stmtCompound() throws RecognitionException {
		StmtCompoundContext _localctx = new StmtCompoundContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_stmtCompound);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(T__6);
			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__3) | (1L << T__4) | (1L << T__6) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__13) | (1L << T__14) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << BASIC_TYPE) | (1L << LITERAL) | (1L << IDENTIFIER))) != 0)) {
				{
				{
				setState(70);
				stmt();
				}
				}
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(76);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtContext extends ParserRuleContext {
		public StmtEmptyContext stmtEmpty() {
			return getRuleContext(StmtEmptyContext.class,0);
		}
		public StmtCompoundContext stmtCompound() {
			return getRuleContext(StmtCompoundContext.class,0);
		}
		public StmtBranchContext stmtBranch() {
			return getRuleContext(StmtBranchContext.class,0);
		}
		public StmtLoopContext stmtLoop() {
			return getRuleContext(StmtLoopContext.class,0);
		}
		public StmtExprContext stmtExpr() {
			return getRuleContext(StmtExprContext.class,0);
		}
		public StmtReturnContext stmtReturn() {
			return getRuleContext(StmtReturnContext.class,0);
		}
		public StmtDeclarationContext stmtDeclaration() {
			return getRuleContext(StmtDeclarationContext.class,0);
		}
		public StmtAssignmentContext stmtAssignment() {
			return getRuleContext(StmtAssignmentContext.class,0);
		}
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_stmt);
		try {
			setState(86);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(78);
				stmtEmpty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(79);
				stmtCompound();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(80);
				stmtBranch();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(81);
				stmtLoop();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(82);
				stmtExpr();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(83);
				stmtReturn();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(84);
				stmtDeclaration();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(85);
				stmtAssignment();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtEmptyContext extends ParserRuleContext {
		public StmtEmptyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtEmpty; }
	}

	public final StmtEmptyContext stmtEmpty() throws RecognitionException {
		StmtEmptyContext _localctx = new StmtEmptyContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_stmtEmpty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtDeclarationContext extends ParserRuleContext {
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public StmtDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtDeclaration; }
	}

	public final StmtDeclarationContext stmtDeclaration() throws RecognitionException {
		StmtDeclarationContext _localctx = new StmtDeclarationContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_stmtDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			declaration();
			setState(91);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtAssignmentContext extends ParserRuleContext {
		public ExprContext var;
		public ExprContext value;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public StmtAssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtAssignment; }
	}

	public final StmtAssignmentContext stmtAssignment() throws RecognitionException {
		StmtAssignmentContext _localctx = new StmtAssignmentContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_stmtAssignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			((StmtAssignmentContext)_localctx).var = expr(0);
			setState(94);
			match(T__9);
			setState(95);
			((StmtAssignmentContext)_localctx).value = expr(0);
			setState(96);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtReturnContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtReturnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtReturn; }
	}

	public final StmtReturnContext stmtReturn() throws RecognitionException {
		StmtReturnContext _localctx = new StmtReturnContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_stmtReturn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			match(T__10);
			setState(100);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << LITERAL) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(99);
				expr(0);
				}
			}

			setState(102);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtExprContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtExpr; }
	}

	public final StmtExprContext stmtExpr() throws RecognitionException {
		StmtExprContext _localctx = new StmtExprContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_stmtExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			expr(0);
			setState(105);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtBranchContext extends ParserRuleContext {
		public ExprContext condition;
		public StmtContext thenBody;
		public StmtContext elseBody;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public StmtBranchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtBranch; }
	}

	public final StmtBranchContext stmtBranch() throws RecognitionException {
		StmtBranchContext _localctx = new StmtBranchContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_stmtBranch);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(T__11);
			setState(108);
			match(T__0);
			setState(109);
			((StmtBranchContext)_localctx).condition = expr(0);
			setState(110);
			match(T__1);
			setState(111);
			((StmtBranchContext)_localctx).thenBody = stmt();
			setState(114);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(112);
				match(T__12);
				setState(113);
				((StmtBranchContext)_localctx).elseBody = stmt();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtLoopContext extends ParserRuleContext {
		public StmtWhileLoopContext stmtWhileLoop() {
			return getRuleContext(StmtWhileLoopContext.class,0);
		}
		public StmtDoWhileLoopContext stmtDoWhileLoop() {
			return getRuleContext(StmtDoWhileLoopContext.class,0);
		}
		public StmtLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtLoop; }
	}

	public final StmtLoopContext stmtLoop() throws RecognitionException {
		StmtLoopContext _localctx = new StmtLoopContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_stmtLoop);
		try {
			setState(118);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__13:
				enterOuterAlt(_localctx, 1);
				{
				setState(116);
				stmtWhileLoop();
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 2);
				{
				setState(117);
				stmtDoWhileLoop();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtWhileLoopContext extends ParserRuleContext {
		public ExprContext condition;
		public StmtContext body;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public StmtWhileLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtWhileLoop; }
	}

	public final StmtWhileLoopContext stmtWhileLoop() throws RecognitionException {
		StmtWhileLoopContext _localctx = new StmtWhileLoopContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_stmtWhileLoop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			match(T__13);
			setState(121);
			match(T__0);
			setState(122);
			((StmtWhileLoopContext)_localctx).condition = expr(0);
			setState(123);
			match(T__1);
			setState(124);
			((StmtWhileLoopContext)_localctx).body = stmt();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtDoWhileLoopContext extends ParserRuleContext {
		public StmtContext body;
		public ExprContext condition;
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtDoWhileLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtDoWhileLoop; }
	}

	public final StmtDoWhileLoopContext stmtDoWhileLoop() throws RecognitionException {
		StmtDoWhileLoopContext _localctx = new StmtDoWhileLoopContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_stmtDoWhileLoop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(T__14);
			setState(127);
			((StmtDoWhileLoopContext)_localctx).body = stmt();
			setState(128);
			match(T__13);
			setState(129);
			match(T__0);
			setState(130);
			((StmtDoWhileLoopContext)_localctx).condition = expr(0);
			setState(131);
			match(T__1);
			setState(132);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext l;
		public ExprContext nested;
		public Token var;
		public Token lit;
		public Token op;
		public ExprListContext params;
		public ExprContext r;
		public Token post_op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode IDENTIFIER() { return getToken(SimpleCParser.IDENTIFIER, 0); }
		public TerminalNode LITERAL() { return getToken(SimpleCParser.LITERAL, 0); }
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(135);
				match(T__0);
				setState(136);
				((ExprContext)_localctx).nested = expr(0);
				setState(137);
				match(T__1);
				}
				break;
			case 2:
				{
				setState(139);
				((ExprContext)_localctx).var = match(IDENTIFIER);
				}
				break;
			case 3:
				{
				setState(140);
				((ExprContext)_localctx).lit = match(LITERAL);
				}
				break;
			case 4:
				{
				setState(141);
				((ExprContext)_localctx).var = match(IDENTIFIER);
				setState(142);
				((ExprContext)_localctx).op = match(T__0);
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << LITERAL) | (1L << IDENTIFIER))) != 0)) {
					{
					setState(143);
					((ExprContext)_localctx).params = exprList();
					}
				}

				setState(146);
				match(T__1);
				}
				break;
			case 5:
				{
				setState(147);
				((ExprContext)_localctx).var = match(IDENTIFIER);
				setState(148);
				((ExprContext)_localctx).op = match(T__15);
				setState(149);
				((ExprContext)_localctx).r = expr(0);
				setState(150);
				match(T__16);
				}
				break;
			case 6:
				{
				setState(152);
				((ExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21))) != 0)) ) {
					((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(153);
				((ExprContext)_localctx).r = expr(10);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(187);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(185);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
					case 1:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(156);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(157);
						((ExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__22) | (1L << T__23))) != 0)) ) {
							((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(158);
						((ExprContext)_localctx).r = expr(10);
						}
						break;
					case 2:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(159);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(160);
						((ExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__19 || _la==T__24) ) {
							((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(161);
						((ExprContext)_localctx).r = expr(9);
						}
						break;
					case 3:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(162);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(163);
						((ExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__25 || _la==T__26) ) {
							((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(164);
						((ExprContext)_localctx).r = expr(8);
						}
						break;
					case 4:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(165);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(166);
						((ExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31) | (1L << T__32))) != 0)) ) {
							((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(167);
						((ExprContext)_localctx).r = expr(7);
						}
						break;
					case 5:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(168);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(169);
						((ExprContext)_localctx).op = match(T__33);
						setState(170);
						((ExprContext)_localctx).r = expr(6);
						}
						break;
					case 6:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(171);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(172);
						((ExprContext)_localctx).op = match(T__34);
						setState(173);
						((ExprContext)_localctx).r = expr(5);
						}
						break;
					case 7:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(174);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(175);
						((ExprContext)_localctx).op = match(T__35);
						setState(176);
						((ExprContext)_localctx).r = expr(4);
						}
						break;
					case 8:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(177);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(178);
						((ExprContext)_localctx).op = match(T__36);
						setState(179);
						((ExprContext)_localctx).r = expr(3);
						}
						break;
					case 9:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(180);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(181);
						((ExprContext)_localctx).op = match(T__37);
						setState(182);
						((ExprContext)_localctx).r = expr(2);
						}
						break;
					case 10:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.l = _prevctx;
						_localctx.l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(183);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(184);
						((ExprContext)_localctx).post_op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__17 || _la==T__18) ) {
							((ExprContext)_localctx).post_op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					}
					} 
				}
				setState(189);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExprListContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprList; }
	}

	public final ExprListContext exprList() throws RecognitionException {
		ExprListContext _localctx = new ExprListContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_exprList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			expr(0);
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(191);
				match(T__2);
				setState(192);
				expr(0);
				}
				}
				setState(197);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 16:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 9);
		case 1:
			return precpred(_ctx, 8);
		case 2:
			return precpred(_ctx, 7);
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 3);
		case 7:
			return precpred(_ctx, 2);
		case 8:
			return precpred(_ctx, 1);
		case 9:
			return precpred(_ctx, 11);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3.\u00c9\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\3\2\7\2(\n\2\f\2\16\2+\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4"+
		"\5\4\65\n\4\3\4\3\4\7\49\n\4\f\4\16\4<\13\4\3\5\3\5\3\5\3\6\5\6B\n\6\3"+
		"\6\3\6\5\6F\n\6\3\7\3\7\7\7J\n\7\f\7\16\7M\13\7\3\7\3\7\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\5\bY\n\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3"+
		"\13\3\f\3\f\5\fg\n\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\5\16u\n\16\3\17\3\17\5\17y\n\17\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3"+
		"\22\3\22\3\22\3\22\5\22\u0093\n\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\5\22\u009d\n\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\7\22\u00bc\n\22\f\22\16\22\u00bf\13\22\3\23"+
		"\3\23\3\23\7\23\u00c4\n\23\f\23\16\23\u00c7\13\23\3\23\2\3\"\24\2\4\6"+
		"\b\n\f\16\20\22\24\26\30\32\34\36 \"$\2\t\3\2\6\7\3\2\24\30\4\2\b\b\31"+
		"\32\4\2\26\26\33\33\3\2\34\35\3\2\36#\3\2\24\25\2\u00d7\2)\3\2\2\2\4,"+
		"\3\2\2\2\6\64\3\2\2\2\b=\3\2\2\2\nA\3\2\2\2\fG\3\2\2\2\16X\3\2\2\2\20"+
		"Z\3\2\2\2\22\\\3\2\2\2\24_\3\2\2\2\26d\3\2\2\2\30j\3\2\2\2\32m\3\2\2\2"+
		"\34x\3\2\2\2\36z\3\2\2\2 \u0080\3\2\2\2\"\u009c\3\2\2\2$\u00c0\3\2\2\2"+
		"&(\5\4\3\2\'&\3\2\2\2(+\3\2\2\2)\'\3\2\2\2)*\3\2\2\2*\3\3\2\2\2+)\3\2"+
		"\2\2,-\5\n\6\2-.\7+\2\2./\7\3\2\2/\60\5\6\4\2\60\61\7\4\2\2\61\62\5\f"+
		"\7\2\62\5\3\2\2\2\63\65\5\b\5\2\64\63\3\2\2\2\64\65\3\2\2\2\65:\3\2\2"+
		"\2\66\67\7\5\2\2\679\5\b\5\28\66\3\2\2\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2"+
		";\7\3\2\2\2<:\3\2\2\2=>\5\n\6\2>?\7+\2\2?\t\3\2\2\2@B\t\2\2\2A@\3\2\2"+
		"\2AB\3\2\2\2BC\3\2\2\2CE\7)\2\2DF\7\b\2\2ED\3\2\2\2EF\3\2\2\2F\13\3\2"+
		"\2\2GK\7\t\2\2HJ\5\16\b\2IH\3\2\2\2JM\3\2\2\2KI\3\2\2\2KL\3\2\2\2LN\3"+
		"\2\2\2MK\3\2\2\2NO\7\n\2\2O\r\3\2\2\2PY\5\20\t\2QY\5\f\7\2RY\5\32\16\2"+
		"SY\5\34\17\2TY\5\30\r\2UY\5\26\f\2VY\5\22\n\2WY\5\24\13\2XP\3\2\2\2XQ"+
		"\3\2\2\2XR\3\2\2\2XS\3\2\2\2XT\3\2\2\2XU\3\2\2\2XV\3\2\2\2XW\3\2\2\2Y"+
		"\17\3\2\2\2Z[\7\13\2\2[\21\3\2\2\2\\]\5\b\5\2]^\7\13\2\2^\23\3\2\2\2_"+
		"`\5\"\22\2`a\7\f\2\2ab\5\"\22\2bc\7\13\2\2c\25\3\2\2\2df\7\r\2\2eg\5\""+
		"\22\2fe\3\2\2\2fg\3\2\2\2gh\3\2\2\2hi\7\13\2\2i\27\3\2\2\2jk\5\"\22\2"+
		"kl\7\13\2\2l\31\3\2\2\2mn\7\16\2\2no\7\3\2\2op\5\"\22\2pq\7\4\2\2qt\5"+
		"\16\b\2rs\7\17\2\2su\5\16\b\2tr\3\2\2\2tu\3\2\2\2u\33\3\2\2\2vy\5\36\20"+
		"\2wy\5 \21\2xv\3\2\2\2xw\3\2\2\2y\35\3\2\2\2z{\7\20\2\2{|\7\3\2\2|}\5"+
		"\"\22\2}~\7\4\2\2~\177\5\16\b\2\177\37\3\2\2\2\u0080\u0081\7\21\2\2\u0081"+
		"\u0082\5\16\b\2\u0082\u0083\7\20\2\2\u0083\u0084\7\3\2\2\u0084\u0085\5"+
		"\"\22\2\u0085\u0086\7\4\2\2\u0086\u0087\7\13\2\2\u0087!\3\2\2\2\u0088"+
		"\u0089\b\22\1\2\u0089\u008a\7\3\2\2\u008a\u008b\5\"\22\2\u008b\u008c\7"+
		"\4\2\2\u008c\u009d\3\2\2\2\u008d\u009d\7+\2\2\u008e\u009d\7*\2\2\u008f"+
		"\u0090\7+\2\2\u0090\u0092\7\3\2\2\u0091\u0093\5$\23\2\u0092\u0091\3\2"+
		"\2\2\u0092\u0093\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u009d\7\4\2\2\u0095"+
		"\u0096\7+\2\2\u0096\u0097\7\22\2\2\u0097\u0098\5\"\22\2\u0098\u0099\7"+
		"\23\2\2\u0099\u009d\3\2\2\2\u009a\u009b\t\3\2\2\u009b\u009d\5\"\22\f\u009c"+
		"\u0088\3\2\2\2\u009c\u008d\3\2\2\2\u009c\u008e\3\2\2\2\u009c\u008f\3\2"+
		"\2\2\u009c\u0095\3\2\2\2\u009c\u009a\3\2\2\2\u009d\u00bd\3\2\2\2\u009e"+
		"\u009f\f\13\2\2\u009f\u00a0\t\4\2\2\u00a0\u00bc\5\"\22\f\u00a1\u00a2\f"+
		"\n\2\2\u00a2\u00a3\t\5\2\2\u00a3\u00bc\5\"\22\13\u00a4\u00a5\f\t\2\2\u00a5"+
		"\u00a6\t\6\2\2\u00a6\u00bc\5\"\22\n\u00a7\u00a8\f\b\2\2\u00a8\u00a9\t"+
		"\7\2\2\u00a9\u00bc\5\"\22\t\u00aa\u00ab\f\7\2\2\u00ab\u00ac\7$\2\2\u00ac"+
		"\u00bc\5\"\22\b\u00ad\u00ae\f\6\2\2\u00ae\u00af\7%\2\2\u00af\u00bc\5\""+
		"\22\7\u00b0\u00b1\f\5\2\2\u00b1\u00b2\7&\2\2\u00b2\u00bc\5\"\22\6\u00b3"+
		"\u00b4\f\4\2\2\u00b4\u00b5\7\'\2\2\u00b5\u00bc\5\"\22\5\u00b6\u00b7\f"+
		"\3\2\2\u00b7\u00b8\7(\2\2\u00b8\u00bc\5\"\22\4\u00b9\u00ba\f\r\2\2\u00ba"+
		"\u00bc\t\b\2\2\u00bb\u009e\3\2\2\2\u00bb\u00a1\3\2\2\2\u00bb\u00a4\3\2"+
		"\2\2\u00bb\u00a7\3\2\2\2\u00bb\u00aa\3\2\2\2\u00bb\u00ad\3\2\2\2\u00bb"+
		"\u00b0\3\2\2\2\u00bb\u00b3\3\2\2\2\u00bb\u00b6\3\2\2\2\u00bb\u00b9\3\2"+
		"\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00be\3\2\2\2\u00be"+
		"#\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00c5\5\"\22\2\u00c1\u00c2\7\5\2\2"+
		"\u00c2\u00c4\5\"\22\2\u00c3\u00c1\3\2\2\2\u00c4\u00c7\3\2\2\2\u00c5\u00c3"+
		"\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6%\3\2\2\2\u00c7\u00c5\3\2\2\2\21)\64"+
		":AEKXftx\u0092\u009c\u00bb\u00bd\u00c5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}