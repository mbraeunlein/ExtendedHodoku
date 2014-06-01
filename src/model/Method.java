
/*package model;

public enum Method {
	FullHouse, 
	NakedSingles, 
	LockedCandidates1, 
	WWing, 
	XYWing, 
	ALSXZ, 
	ALSXY;
}
*/

package model;

public enum Method {
	FullHouse, NakedSingles, HiddenSingles, // fill in digits
	LockedCandidates1, LockedCandidates2, NakedPairs, NakedTriples, NakedQuadruples, HiddenPairs, HiddenTriples, HiddenQuadruples, // simple
	WWing, XYWing, XYZWing, // wings
	Skyscarper, TwoStringKite, TurbotFish, EmptyRectangle, // single digit patterns
	XWing, Swordfish, Jellyfish, // fishes
	//SashimiXWing, SashimiSwordfish, SashimiJellyfish,
	//FinnedXWing, FinnedSwordfish, FinnedJellyfish,
	//UniquenessTest1, UniquenessTest2, UniquenessTest3, UniquenessTest4, UniquenessTest5, UniquenessTest6, HiddenRectangle, AvoidableRectangle1, AvoidableRectangle2, // uniqueness
	SueDeCoq,
	SimpleColors, //MultiColors, // Coloring 
	//XChain, XYChain, RemotePairs, // Chains
	ALSXZ, ALSXY, ALSChains, // Almost Locked Sets
	Backtracking;
}
