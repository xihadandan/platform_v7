     
    var PpWindowState = {
        ppWindowNormal : 1,
        ppWindowMinimized : 2,
        ppWindowMaximized : 3
    }

     
    var PpArrangeStyle = {
        ppArrangeTiled : 1,
        ppArrangeCascade : 2
    }

     
    var PpViewType = {
        ppViewSlide : 1,
        ppViewSlideMaster : 2,
        ppViewNotesPage : 3,
        ppViewHandoutMaster : 4,
        ppViewNotesMaster : 5,
        ppViewOutline : 6,
        ppViewSlideSorter : 7,
        ppViewTitleMaster : 8,
        ppViewNormal : 9,
        ppViewPrintPreview : 10,
        ppViewThumbnails : 11,
        ppViewMasterThumbnails : 12
    }

     
    var PpColorSchemeIndex = {
        ppSchemeColorMixed : -2,
        ppNotSchemeColor : 0,
        ppBackground : 1,
        ppForeground : 2,
        ppShadow : 3,
        ppTitle : 4,
        ppFill : 5,
        ppAccent1 : 6,
        ppAccent2 : 7,
        ppAccent3 : 8
    }

     
    var PpSlideSizeType = {
        ppSlideSizeOnScreen : 1,
        ppSlideSizeLetterPaper : 2,
        ppSlideSizeA4Paper : 3,
        ppSlideSize35MM : 4,
        ppSlideSizeOverhead : 5,
        ppSlideSizeBanner : 6,
        ppSlideSizeCustom : 7,
        ppSlideSizeLedgerPaper : 8,
        ppSlideSizeA3Paper : 9,
        ppSlideSizeB4ISOPaper : 10,
        ppSlideSizeB5ISOPaper : 11,
        ppSlideSizeB4JISPaper : 12,
        ppSlideSizeB5JISPaper : 13,
        ppSlideSizeHagakiCard : 14,
        ppSlideSizeOnScreen16x9 : 15,
        ppSlideSizeOnScreen16x10 : 16
    }

     
    var PpSaveAsFileType = {
        ppSaveAsPresentation : 1,
        ppSaveAsPowerPoint7 : 2,
        ppSaveAsPowerPoint4 : 3,
        ppSaveAsPowerPoint3 : 4,
        ppSaveAsTemplate : 5,
        ppSaveAsRTF : 6,
        ppSaveAsShow : 7,
        ppSaveAsAddIn : 8,
        ppSaveAsPowerPoint4FarEast : 10,
        ppSaveAsDefault : 11,
        ppSaveAsHTML : 12,
        ppSaveAsHTMLv3 : 13,
        ppSaveAsHTMLDual : 14,
        ppSaveAsMetaFile : 15,
        ppSaveAsGIF : 16,
        ppSaveAsJPG : 17,
        ppSaveAsPNG : 18,
        ppSaveAsBMP : 19,
        ppSaveAsWebArchive : 20,
        ppSaveAsTIF : 21,
        ppSaveAsPresForReview : 22,
        ppSaveAsEMF : 23,
        ppSaveAsOpenXMLPresentation : 24,
        ppSaveAsOpenXMLPresentationMacroEnabled : 25,
        ppSaveAsOpenXMLTemplate : 26,
        ppSaveAsOpenXMLTemplateMacroEnabled : 27,
        ppSaveAsOpenXMLShow : 28,
        ppSaveAsOpenXMLShowMacroEnabled : 29,
        ppSaveAsOpenXMLAddin : 30,
        ppSaveAsOpenXMLTheme : 31,
        ppSaveAsPDF : 32,
        ppSaveAsXPS : 33,
        ppSaveAsXMLPresentation : 34,
        ppSaveAsOpenDocumentPresentation : 35,
        ppSaveAsOpenXMLPicturePresentation : 36,
        ppSaveAsWMV : 37,
        ppSaveAsStrictOpenXMLPresentation : 38,
        ppSaveAsMP4 : 39,
        ppSaveAsExternalConverter : 64000
    }

     
    var PpTextStyleType = {
        ppDefaultStyle : 1,
        ppTitleStyle : 2,
        ppBodyStyle : 3
    }

     
    var PpSlideLayout = {
        ppLayoutMixed : -2,
        ppLayoutTitle : 1,
        ppLayoutText : 2,
        ppLayoutTwoColumnText : 3,
        ppLayoutTable : 4,
        ppLayoutTextAndChart : 5,
        ppLayoutChartAndText : 6,
        ppLayoutOrgchart : 7,
        ppLayoutChart : 8,
        ppLayoutTextAndClipart : 9,
        ppLayoutClipartAndText : 10,
        ppLayoutTitleOnly : 11,
        ppLayoutBlank : 12,
        ppLayoutTextAndObject : 13,
        ppLayoutObjectAndText : 14,
        ppLayoutLargeObject : 15,
        ppLayoutObject : 16,
        ppLayoutTextAndMediaClip : 17,
        ppLayoutMediaClipAndText : 18,
        ppLayoutObjectOverText : 19,
        ppLayoutTextOverObject : 20,
        ppLayoutTextAndTwoObjects : 21,
        ppLayoutTwoObjectsAndText : 22,
        ppLayoutTwoObjectsOverText : 23,
        ppLayoutFourObjects : 24,
        ppLayoutVerticalText : 25,
        ppLayoutClipArtAndVerticalText : 26,
        ppLayoutVerticalTitleAndText : 27,
        ppLayoutVerticalTitleAndTextOverChart : 28,
        ppLayoutTwoObjects : 29,
        ppLayoutObjectAndTwoObjects : 30,
        ppLayoutTwoObjectsAndObject : 31,
        ppLayoutCustom : 32,
        ppLayoutSectionHeader : 33,
        ppLayoutComparison : 34,
        ppLayoutContentWithCaption : 35,
        ppLayoutPictureWithCaption : 36
    }

     
    var PpEntryEffect = {
        ppEffectMixed : -2,
        ppEffectNone : 0,
        ppEffectCut : 257,
        ppEffectCutThroughBlack : 258,
        ppEffectRandom : 513,
        ppEffectBlindsHorizontal : 769,
        ppEffectBlindsVertical : 770,
        ppEffectCheckerboardAcross : 1025,
        ppEffectCheckerboardDown : 1026,
        ppEffectCoverLeft : 1281,
        ppEffectCoverUp : 1282,
        ppEffectCoverRight : 1283,
        ppEffectCoverDown : 1284,
        ppEffectCoverLeftUp : 1285,
        ppEffectCoverRightUp : 1286,
        ppEffectCoverLeftDown : 1287,
        ppEffectCoverRightDown : 1288,
        ppEffectDissolve : 1537,
        ppEffectFade : 1793,
        ppEffectUncoverLeft : 2049,
        ppEffectUncoverUp : 2050,
        ppEffectUncoverRight : 2051,
        ppEffectUncoverDown : 2052,
        ppEffectUncoverLeftUp : 2053,
        ppEffectUncoverRightUp : 2054,
        ppEffectUncoverLeftDown : 2055,
        ppEffectUncoverRightDown : 2056,
        ppEffectRandomBarsHorizontal : 2305,
        ppEffectRandomBarsVertical : 2306,
        ppEffectStripsUpLeft : 2561,
        ppEffectStripsUpRight : 2562,
        ppEffectStripsDownLeft : 2563,
        ppEffectStripsDownRight : 2564,
        ppEffectStripsLeftUp : 2565,
        ppEffectStripsRightUp : 2566,
        ppEffectStripsLeftDown : 2567,
        ppEffectStripsRightDown : 2568,
        ppEffectWipeLeft : 2817,
        ppEffectWipeUp : 2818,
        ppEffectWipeRight : 2819,
        ppEffectWipeDown : 2820,
        ppEffectBoxOut : 3073,
        ppEffectBoxIn : 3074,
        ppEffectFlyFromLeft : 3329,
        ppEffectFlyFromTop : 3330,
        ppEffectFlyFromRight : 3331,
        ppEffectFlyFromBottom : 3332,
        ppEffectFlyFromTopLeft : 3333,
        ppEffectFlyFromTopRight : 3334,
        ppEffectFlyFromBottomLeft : 3335,
        ppEffectFlyFromBottomRight : 3336,
        ppEffectPeekFromLeft : 3337,
        ppEffectPeekFromDown : 3338,
        ppEffectPeekFromRight : 3339,
        ppEffectPeekFromUp : 3340,
        ppEffectCrawlFromLeft : 3341,
        ppEffectCrawlFromUp : 3342,
        ppEffectCrawlFromRight : 3343,
        ppEffectCrawlFromDown : 3344,
        ppEffectZoomIn : 3345,
        ppEffectZoomInSlightly : 3346,
        ppEffectZoomOut : 3347,
        ppEffectZoomOutSlightly : 3348,
        ppEffectZoomCenter : 3349,
        ppEffectZoomBottom : 3350,
        ppEffectStretchAcross : 3351,
        ppEffectStretchLeft : 3352,
        ppEffectStretchUp : 3353,
        ppEffectStretchRight : 3354,
        ppEffectStretchDown : 3355,
        ppEffectSwivel : 3356,
        ppEffectSpiral : 3357,
        ppEffectSplitHorizontalOut : 3585,
        ppEffectSplitHorizontalIn : 3586,
        ppEffectSplitVerticalOut : 3587,
        ppEffectSplitVerticalIn : 3588,
        ppEffectFlashOnceFast : 3841,
        ppEffectFlashOnceMedium : 3842,
        ppEffectFlashOnceSlow : 3843,
        ppEffectAppear : 3844,
        ppEffectCircleOut : 3845,
        ppEffectDiamondOut : 3846,
        ppEffectCombHorizontal : 3847,
        ppEffectCombVertical : 3848,
        ppEffectFadeSmoothly : 3849,
        ppEffectNewsflash : 3850,
        ppEffectPlusOut : 3851,
        ppEffectPushDown : 3852,
        ppEffectPushLeft : 3853,
        ppEffectPushRight : 3854,
        ppEffectPushUp : 3855,
        ppEffectWedge : 3856,
        ppEffectWheel1Spoke : 3857,
        ppEffectWheel2Spokes : 3858,
        ppEffectWheel3Spokes : 3859,
        ppEffectWheel4Spokes : 3860,
        ppEffectWheel8Spokes : 3861,
        ppEffectWheelReverse1Spoke : 3862,
        ppEffectVortexLeft : 3863,
        ppEffectVortexUp : 3864,
        ppEffectVortexRight : 3865,
        ppEffectVortexDown : 3866,
        ppEffectRippleCenter : 3867,
        ppEffectRippleRightUp : 3868,
        ppEffectRippleLeftUp : 3869,
        ppEffectRippleLeftDown : 3870,
        ppEffectRippleRightDown : 3871,
        ppEffectGlitterDiamondLeft : 3872,
        ppEffectGlitterDiamondUp : 3873,
        ppEffectGlitterDiamondRight : 3874,
        ppEffectGlitterDiamondDown : 3875,
        ppEffectGlitterHexagonLeft : 3876,
        ppEffectGlitterHexagonUp : 3877,
        ppEffectGlitterHexagonRight : 3878,
        ppEffectGlitterHexagonDown : 3879,
        ppEffectGalleryLeft : 3880,
        ppEffectGalleryRight : 3881,
        ppEffectConveyorLeft : 3882,
        ppEffectConveyorRight : 3883,
        ppEffectDoorsVertical : 3884,
        ppEffectDoorsHorizontal : 3885,
        ppEffectWindowVertical : 3886,
        ppEffectWindowHorizontal : 3887,
        ppEffectWarpIn : 3888,
        ppEffectWarpOut : 3889,
        ppEffectFlyThroughIn : 3890,
        ppEffectFlyThroughOut : 3891,
        ppEffectFlyThroughInBounce : 3892,
        ppEffectFlyThroughOutBounce : 3893,
        ppEffectRevealSmoothLeft : 3894,
        ppEffectRevealSmoothRight : 3895,
        ppEffectRevealBlackLeft : 3896,
        ppEffectRevealBlackRight : 3897,
        ppEffectHoneycomb : 3898,
        ppEffectFerrisWheelLeft : 3899,
        ppEffectFerrisWheelRight : 3900,
        ppEffectSwitchLeft : 3901,
        ppEffectSwitchUp : 3902,
        ppEffectSwitchRight : 3903,
        ppEffectSwitchDown : 3904,
        ppEffectFlipLeft : 3905,
        ppEffectFlipUp : 3906,
        ppEffectFlipRight : 3907,
        ppEffectFlipDown : 3908,
        ppEffectFlashbulb : 3909,
        ppEffectShredStripsIn : 3910,
        ppEffectShredStripsOut : 3911,
        ppEffectShredRectangleIn : 3912,
        ppEffectShredRectangleOut : 3913,
        ppEffectCubeLeft : 3914,
        ppEffectCubeUp : 3915,
        ppEffectCubeRight : 3916,
        ppEffectCubeDown : 3917,
        ppEffectRotateLeft : 3918,
        ppEffectRotateUp : 3919,
        ppEffectRotateRight : 3920,
        ppEffectRotateDown : 3921,
        ppEffectBoxLeft : 3922,
        ppEffectBoxUp : 3923,
        ppEffectBoxRight : 3924,
        ppEffectBoxDown : 3925,
        ppEffectOrbitLeft : 3926,
        ppEffectOrbitUp : 3927,
        ppEffectOrbitRight : 3928,
        ppEffectOrbitDown : 3929,
        ppEffectPanLeft : 3930,
        ppEffectPanUp : 3931,
        ppEffectPanRight : 3932,
        ppEffectPanDown : 3933,
        ppEffectFallOverLeft : 3934,
        ppEffectFallOverRight : 3935,
        ppEffectDrapeLeft : 3936,
        ppEffectDrapeRight : 3937,
        ppEffectCurtains : 3938,
        ppEffectWindLeft : 3939,
        ppEffectWindRight : 3940,
        ppEffectPrestige : 3941,
        ppEffectFracture : 3942,
        ppEffectCrush : 3943,
        ppEffectPeelOffLeft : 3944,
        ppEffectPeelOffRight : 3945,
        ppEffectPageCurlSingleLeft : 3946,
        ppEffectPageCurlSingleRight : 3947,
        ppEffectPageCurlDoubleLeft : 3948,
        ppEffectPageCurlDoubleRight : 3949,
        ppEffectAirplaneLeft : 3950,
        ppEffectAirplaneRight : 3951,
        ppEffectOrigamiLeft : 3952,
        ppEffectOrigamiRight : 3953
    }

     
    var PpTextLevelEffect = {
        ppAnimateLevelMixed : -2,
        ppAnimateLevelNone : 0,
        ppAnimateByFirstLevel : 1,
        ppAnimateBySecondLevel : 2,
        ppAnimateByThirdLevel : 3,
        ppAnimateByFourthLevel : 4,
        ppAnimateByFifthLevel : 5,
        ppAnimateByAllLevels : 16
    }

     
    var PpTextUnitEffect = {
        ppAnimateUnitMixed : -2,
        ppAnimateByParagraph : 0,
        ppAnimateByWord : 1,
        ppAnimateByCharacter : 2
    }

     
    var PpChartUnitEffect = {
        ppAnimateChartMixed : -2,
        ppAnimateBySeries : 1,
        ppAnimateByCategory : 2,
        ppAnimateBySeriesElements : 3,
        ppAnimateByCategoryElements : 4,
        ppAnimateChartAllAtOnce : 5
    }

     
    var PpAfterEffect = {
        ppAfterEffectMixed : -2,
        ppAfterEffectNothing : 0,
        ppAfterEffectHide : 1,
        ppAfterEffectDim : 2,
        ppAfterEffectHideOnClick : 3
    }

     
    var PpAdvanceMode = {
        ppAdvanceModeMixed : -2,
        ppAdvanceOnClick : 1,
        ppAdvanceOnTime : 2
    }

     
    var PpSoundEffectType = {
        ppSoundEffectsMixed : -2,
        ppSoundNone : 0,
        ppSoundStopPrevious : 1,
        ppSoundFile : 2
    }

     
    var PpFollowColors = {
        ppFollowColorsMixed : -2,
        ppFollowColorsNone : 0,
        ppFollowColorsScheme : 1,
        ppFollowColorsTextAndBackground : 2
    }

     
    var PpUpdateOption = {
        ppUpdateOptionMixed : -2,
        ppUpdateOptionManual : 1,
        ppUpdateOptionAutomatic : 2
    }

     
    var PpParagraphAlignment = {
        ppAlignmentMixed : -2,
        ppAlignLeft : 1,
        ppAlignCenter : 2,
        ppAlignRight : 3,
        ppAlignJustify : 4,
        ppAlignDistribute : 5,
        ppAlignThaiDistribute : 6,
        ppAlignJustifyLow : 7
    }

     
    var PpBaselineAlignment = {
        ppBaselineAlignMixed : -2,
        ppBaselineAlignBaseline : 1,
        ppBaselineAlignTop : 2,
        ppBaselineAlignCenter : 3,
        ppBaselineAlignFarEast50 : 4,
        ppBaselineAlignAuto : 5
    }

     
    var PpTabStopType = {
        ppTabStopMixed : -2,
        ppTabStopLeft : 1,
        ppTabStopCenter : 2,
        ppTabStopRight : 3,
        ppTabStopDecimal : 4
    }

     
    var PpIndentControl = {
        ppIndentControlMixed : -2,
        ppIndentReplaceAttr : 1,
        ppIndentKeepAttr : 2
    }

     
    var PpChangeCase = {
        ppCaseSentence : 1,
        ppCaseLower : 2,
        ppCaseUpper : 3,
        ppCaseTitle : 4,
        ppCaseToggle : 5
    }

     
    var PpSlideShowPointerType = {
        ppSlideShowPointerNone : 0,
        ppSlideShowPointerArrow : 1,
        ppSlideShowPointerPen : 2,
        ppSlideShowPointerAlwaysHidden : 3,
        ppSlideShowPointerAutoArrow : 4,
        ppSlideShowPointerEraser : 5
    }

     
    var PpSlideShowState = {
        ppSlideShowRunning : 1,
        ppSlideShowPaused : 2,
        ppSlideShowBlackScreen : 3,
        ppSlideShowWhiteScreen : 4,
        ppSlideShowDone : 5
    }

     
    var PpSlideShowAdvanceMode = {
        ppSlideShowManualAdvance : 1,
        ppSlideShowUseSlideTimings : 2,
        ppSlideShowRehearseNewTimings : 3
    }

     
    var PpFileDialogType = {
        ppFileDialogOpen : 1,
        ppFileDialogSave : 2
    }

     
    var PpPrintOutputType = {
        ppPrintOutputSlides : 1,
        ppPrintOutputTwoSlideHandouts : 2,
        ppPrintOutputThreeSlideHandouts : 3,
        ppPrintOutputSixSlideHandouts : 4,
        ppPrintOutputNotesPages : 5,
        ppPrintOutputOutline : 6,
        ppPrintOutputBuildSlides : 7,
        ppPrintOutputFourSlideHandouts : 8,
        ppPrintOutputNineSlideHandouts : 9,
        ppPrintOutputOneSlideHandouts : 10
    }

     
    var PpPrintHandoutOrder = {
        ppPrintHandoutVerticalFirst : 1,
        ppPrintHandoutHorizontalFirst : 2
    }

     
    var PpPrintColorType = {
        ppPrintColor : 1,
        ppPrintBlackAndWhite : 2,
        ppPrintPureBlackAndWhite : 3
    }

     
    var PpSelectionType = {
        ppSelectionNone : 0,
        ppSelectionSlides : 1,
        ppSelectionShapes : 2,
        ppSelectionText : 3
    }

     
    var PpDirection = {
        ppDirectionMixed : -2,
        ppDirectionLeftToRight : 1,
        ppDirectionRightToLeft : 2
    }

     
    var PpDateTimeFormat = {
        ppDateTimeFormatMixed : -2,
        ppDateTimeMdyy : 1,
        ppDateTimeddddMMMMddyyyy : 2,
        ppDateTimedMMMMyyyy : 3,
        ppDateTimeMMMMdyyyy : 4,
        ppDateTimedMMMyy : 5,
        ppDateTimeMMMMyy : 6,
        ppDateTimeMMyy : 7,
        ppDateTimeMMddyyHmm : 8,
        ppDateTimeMMddyyhmmAMPM : 9,
        ppDateTimeHmm : 10,
        ppDateTimeHmmss : 11,
        ppDateTimehmmAMPM : 12,
        ppDateTimehmmssAMPM : 13,
        ppDateTimeFigureOut : 14,
        ppDateTimeUAQ1 : 15,
        ppDateTimeUAQ2 : 16,
        ppDateTimeUAQ3 : 17,
        ppDateTimeUAQ4 : 18,
        ppDateTimeUAQ5 : 19,
        ppDateTimeUAQ6 : 20,
        ppDateTimeUAQ7 : 21
    }

     
    var PpTransitionSpeed = {
        ppTransitionSpeedMixed : -2,
        ppTransitionSpeedSlow : 1,
        ppTransitionSpeedMedium : 2,
        ppTransitionSpeedFast : 3
    }

     
    var PpMouseActivation = {
        ppMouseClick : 1,
        ppMouseOver : 2
    }

     
    var PpActionType = {
        ppActionMixed : -2,
        ppActionNone : 0,
        ppActionNextSlide : 1,
        ppActionPreviousSlide : 2,
        ppActionFirstSlide : 3,
        ppActionLastSlide : 4,
        ppActionLastSlideViewed : 5,
        ppActionEndShow : 6,
        ppActionHyperlink : 7,
        ppActionRunMacro : 8,
        ppActionRunProgram : 9,
        ppActionNamedSlideShow : 10,
        ppActionOLEVerb : 11,
        ppActionPlay : 12
    }

     
    var PpPlaceholderType = {
        ppPlaceholderMixed : -2,
        ppPlaceholderTitle : 1,
        ppPlaceholderBody : 2,
        ppPlaceholderCenterTitle : 3,
        ppPlaceholderSubtitle : 4,
        ppPlaceholderVerticalTitle : 5,
        ppPlaceholderVerticalBody : 6,
        ppPlaceholderObject : 7,
        ppPlaceholderChart : 8,
        ppPlaceholderBitmap : 9,
        ppPlaceholderMediaClip : 10,
        ppPlaceholderOrgChart : 11,
        ppPlaceholderTable : 12,
        ppPlaceholderSlidvarber : 13,
        ppPlaceholderHeader : 14,
        ppPlaceholderFooter : 15,
        ppPlaceholderDate : 16,
        ppPlaceholderVerticalObject : 17,
        ppPlaceholderPicture : 18
    }

     
    var PpSlideShowType = {
        ppShowTypeSpeaker : 1,
        ppShowTypeWindow : 2,
        ppShowTypeKiosk : 3,
        ppShowTypeWindow2 : 4
    }

     
    var PpPrintRangeType = {
        ppPrintAll : 1,
        ppPrintSelection : 2,
        ppPrintCurrent : 3,
        ppPrintSlideRange : 4,
        ppPrintNamedSlideShow : 5,
        ppPrintSection : 6
    }

     
    var PpAutoSize = {
        ppAutoSizeMixed : -2,
        ppAutoSizeNone : 0,
        ppAutoSizeShapeToFitText : 1
    }

     
    var PpMediaType = {
        ppMediaTypeMixed : -2,
        ppMediaTypeOther : 1,
        ppMediaTypeSound : 2,
        ppMediaTypeMovie : 3
    }

     
    var PpSoundFormatType = {
        ppSoundFormatMixed : -2,
        ppSoundFormatNone : 0,
        ppSoundFormatWAV : 1,
        ppSoundFormatMIDI : 2,
        ppSoundFormatCDAudio : 3
    }

     
    var PpFarEastLineBreakLevel = {
        ppFarEastLineBreakLevelNormal : 1,
        ppFarEastLineBreakLevelStrict : 2,
        ppFarEastLineBreakLevelCustom : 3
    }

     
    var PpSlideShowRangeType = {
        ppShowAll : 1,
        ppShowSlideRange : 2,
        ppShowNamedSlideShow : 3
    }

     
    var PpFrameColors = {
        ppFrameColorsBrowserColors : 1,
        ppFrameColorsPresentationSchemeTextColor : 2,
        ppFrameColorsPresentationSchemeAccentColor : 3,
        ppFrameColorsWhiteTextOnBlack : 4,
        ppFrameColorsBlackTextOnWhite : 5
    }

     
    var PpBorderType = {
        ppBorderTop : 1,
        ppBorderLeft : 2,
        ppBorderBottom : 3,
        ppBorderRight : 4,
        ppBorderDiagonalDown : 5,
        ppBorderDiagonalUp : 6
    }

     
    var PpHTMLVersion = {
        ppHTMLv3 : 1,
        ppHTMLv4 : 2,
        ppHTMLDual : 3,
        ppHTMLAutodetect : 4
    }

     
    var PpPublishSourceType = {
        ppPublishAll : 1,
        ppPublishSlideRange : 2,
        ppPublishNamedSlideShow : 3
    }

     
    var PpBulletType = {
        ppBulletMixed : -2,
        ppBulletNone : 0,
        ppBulletUnnumbered : 1,
        ppBulletNumbered : 2,
        ppBulletPicture : 3
    }

     
    var PpNumberedBulletStyle = {
        ppBulletStyleMixed : -2,
        ppBulletAlphaLCPeriod : 0,
        ppBulletAlphaUCPeriod : 1,
        ppBulletArabicParenRight : 2,
        ppBulletArabicPeriod : 3,
        ppBulletRomanLCParenBoth : 4,
        ppBulletRomanLCParenRight : 5,
        ppBulletRomanLCPeriod : 6,
        ppBulletRomanUCPeriod : 7,
        ppBulletAlphaLCParenBoth : 8,
        ppBulletAlphaLCParenRight : 9,
        ppBulletAlphaUCParenBoth : 10,
        ppBulletAlphaUCParenRight : 11,
        ppBulletArabicParenBoth : 12,
        ppBulletArabicPlain : 13,
        ppBulletRomanUCParenBoth : 14,
        ppBulletRomanUCParenRight : 15,
        ppBulletSimpChinPlain : 16,
        ppBulletSimpChinPeriod : 17,
        ppBulletCirclvarDBPlain : 18,
        ppBulletCirclvarWDWhitePlain : 19,
        ppBulletCirclvarWDBlackPlain : 20,
        ppBulletTradChinPlain : 21,
        ppBulletTradChinPeriod : 22,
        ppBulletArabicAlphaDash : 23,
        ppBulletArabicAbjadDash : 24,
        ppBulletHebrewAlphaDash : 25,
        ppBulletKanjiKoreanPlain : 26,
        ppBulletKanjiKoreanPeriod : 27,
        ppBulletArabicDBPlain : 28,
        ppBulletArabicDBPeriod : 29,
        ppBulletThaiAlphaPeriod : 30,
        ppBulletThaiAlphaParenRight : 31,
        ppBulletThaiAlphaParenBoth : 32,
        ppBulletThaiNumPeriod : 33,
        ppBulletThaiNumParenRight : 34,
        ppBulletThaiNumParenBoth : 35,
        ppBulletHindiAlphaPeriod : 36,
        ppBulletHindiNumPeriod : 37,
        ppBulletKanjiSimpChinDBPeriod : 38,
        ppBulletHindiNumParenRight : 39,
        ppBulletHindiAlpha1Period : 40
    }

     
    var PpShapeFormat = {
        ppShapeFormatGIF : 0,
        ppShapeFormatJPG : 1,
        ppShapeFormatPNG : 2,
        ppShapeFormatBMP : 3,
        ppShapeFormatWMF : 4,
        ppShapeFormatEMF : 5
    }

     
    var PpExportMode = {
        ppRelativeToSlide : 1,
        ppClipRelativeToSlide : 2,
        ppScaleToFit : 3,
        ppScaleXY : 4
    }

     
    var PpPasteDataType = {
        ppPasteDefault : 0,
        ppPasteBitmap : 1,
        ppPasteEnhancedMetafile : 2,
        ppPasteMetafilePicture : 3,
        ppPasteGIF : 4,
        ppPasteJPG : 5,
        ppPastePNG : 6,
        ppPasteText : 7,
        ppPasteHTML : 8,
        ppPasteRTF : 9,
        ppPasteOLEObject : 10,
        ppPasteShape : 11
    }

     
    var MsoAnimEffect = {
        msoAnimEffectCustom : 0,
        msoAnimEffectAppear : 1,
        msoAnimEffectFly : 2,
        msoAnimEffectBlinds : 3,
        msoAnimEffectBox : 4,
        msoAnimEffectCheckerboard : 5,
        msoAnimEffectCircle : 6,
        msoAnimEffectCrawl : 7,
        msoAnimEffectDiamond : 8,
        msoAnimEffectDissolve : 9,
        msoAnimEffectFade : 10,
        msoAnimEffectFlashOnce : 11,
        msoAnimEffectPeek : 12,
        msoAnimEffectPlus : 13,
        msoAnimEffectRandomBars : 14,
        msoAnimEffectSpiral : 15,
        msoAnimEffectSplit : 16,
        msoAnimEffectStretch : 17,
        msoAnimEffectStrips : 18,
        msoAnimEffectSwivel : 19,
        msoAnimEffectWedge : 20,
        msoAnimEffectWheel : 21,
        msoAnimEffectWipe : 22,
        msoAnimEffectZoom : 23,
        msoAnimEffectRandomEffects : 24,
        msoAnimEffectBoomerang : 25,
        msoAnimEffectBounce : 26,
        msoAnimEffectColorReveal : 27,
        msoAnimEffectCredits : 28,
        msoAnimEffectEaseIn : 29,
        msoAnimEffectFloat : 30,
        msoAnimEffectGrowAndTurn : 31,
        msoAnimEffectLightSpeed : 32,
        msoAnimEffectPinwheel : 33,
        msoAnimEffectRiseUp : 34,
        msoAnimEffectSwish : 35,
        msoAnimEffectThinLine : 36,
        msoAnimEffectUnfold : 37,
        msoAnimEffectWhip : 38,
        msoAnimEffectAscend : 39,
        msoAnimEffectCenterRevolve : 40,
        msoAnimEffectFadedSwivel : 41,
        msoAnimEffectDescend : 42,
        msoAnimEffectSling : 43,
        msoAnimEffectSpinner : 44,
        msoAnimEffectStretchy : 45,
        msoAnimEffectZip : 46,
        msoAnimEffectArcUp : 47,
        msoAnimEffectFadedZoom : 48,
        msoAnimEffectGlide : 49,
        msoAnimEffectExpand : 50,
        msoAnimEffectFlip : 51,
        msoAnimEffectShimmer : 52,
        msoAnimEffectFold : 53,
        msoAnimEffectChangeFillColor : 54,
        msoAnimEffectChangeFont : 55,
        msoAnimEffectChangeFontColor : 56,
        msoAnimEffectChangeFontSize : 57,
        msoAnimEffectChangeFontStyle : 58,
        msoAnimEffectGrowShrink : 59,
        msoAnimEffectChangeLineColor : 60,
        msoAnimEffectSpin : 61,
        msoAnimEffectTransparency : 62,
        msoAnimEffectBoldFlash : 63,
        msoAnimEffectBlast : 64,
        msoAnimEffectBoldReveal : 65,
        msoAnimEffectBrushOnColor : 66,
        msoAnimEffectBrushOnUnderline : 67,
        msoAnimEffectColorBlend : 68,
        msoAnimEffectColorWave : 69,
        msoAnimEffectComplementaryColor : 70,
        msoAnimEffectComplementaryColor2 : 71,
        msoAnimEffectContrastingColor : 72,
        msoAnimEffectDarken : 73,
        msoAnimEffectDesaturate : 74,
        msoAnimEffectFlashBulb : 75,
        msoAnimEffectFlicker : 76,
        msoAnimEffectGrowWithColor : 77,
        msoAnimEffectLighten : 78,
        msoAnimEffectStyleEmphasis : 79,
        msoAnimEffectTeeter : 80,
        msoAnimEffectVerticalGrow : 81,
        msoAnimEffectWave : 82,
        msoAnimEffectMediaPlay : 83,
        msoAnimEffectMediaPause : 84,
        msoAnimEffectMediaStop : 85,
        msoAnimEffectPathCircle : 86,
        msoAnimEffectPathRightTriangle : 87,
        msoAnimEffectPathDiamond : 88,
        msoAnimEffectPathHexagon : 89,
        msoAnimEffectPath5PointStar : 90,
        msoAnimEffectPathCrescentMoon : 91,
        msoAnimEffectPathSquare : 92,
        msoAnimEffectPathTrapezoid : 93,
        msoAnimEffectPathHeart : 94,
        msoAnimEffectPathOctagon : 95,
        msoAnimEffectPath6PointStar : 96,
        msoAnimEffectPathFootball : 97,
        msoAnimEffectPathEqualTriangle : 98,
        msoAnimEffectPathParallelogram : 99,
        msoAnimEffectPathPentagon : 100,
        msoAnimEffectPath4PointStar : 101,
        msoAnimEffectPath8PointStar : 102,
        msoAnimEffectPathTeardrop : 103,
        msoAnimEffectPathPointyStar : 104,
        msoAnimEffectPathCurvedSquare : 105,
        msoAnimEffectPathCurvedX : 106,
        msoAnimEffectPathVerticalFigure8 : 107,
        msoAnimEffectPathCurvyStar : 108,
        msoAnimEffectPathLoopdeLoop : 109,
        msoAnimEffectPathBuzzsaw : 110,
        msoAnimEffectPathHorizontalFigure8 : 111,
        msoAnimEffectPathPeanut : 112,
        msoAnimEffectPathFigure8Four : 113,
        msoAnimEffectPathNeutron : 114,
        msoAnimEffectPathSwoosh : 115,
        msoAnimEffectPathBean : 116,
        msoAnimEffectPathPlus : 117,
        msoAnimEffectPathInvertedTriangle : 118,
        msoAnimEffectPathInvertedSquare : 119,
        msoAnimEffectPathLeft : 120,
        msoAnimEffectPathTurnRight : 121,
        msoAnimEffectPathArcDown : 122,
        msoAnimEffectPathZigzag : 123,
        msoAnimEffectPathSCurve2 : 124,
        msoAnimEffectPathSineWave : 125,
        msoAnimEffectPathBounceLeft : 126,
        msoAnimEffectPathDown : 127,
        msoAnimEffectPathTurnUp : 128,
        msoAnimEffectPathArcUp : 129,
        msoAnimEffectPathHeartbeat : 130,
        msoAnimEffectPathSpiralRight : 131,
        msoAnimEffectPathWave : 132,
        msoAnimEffectPathCurvyLeft : 133,
        msoAnimEffectPathDiagonalDownRight : 134,
        msoAnimEffectPathTurnDown : 135,
        msoAnimEffectPathArcLeft : 136,
        msoAnimEffectPathFunnel : 137,
        msoAnimEffectPathSpring : 138,
        msoAnimEffectPathBounceRight : 139,
        msoAnimEffectPathSpiralLeft : 140,
        msoAnimEffectPathDiagonalUpRight : 141,
        msoAnimEffectPathTurnUpRight : 142,
        msoAnimEffectPathArcRight : 143,
        msoAnimEffectPathSCurve1 : 144,
        msoAnimEffectPathDecayingWave : 145,
        msoAnimEffectPathCurvyRight : 146,
        msoAnimEffectPathStairsDown : 147,
        msoAnimEffectPathUp : 148,
        msoAnimEffectPathRight : 149,
        msoAnimEffectMediaPlayFromBookmark : 150
    }

     
    var MsoAnimateByLevel = {
        msoAnimateLevelMixed : -1,
        msoAnimateLevelNone : 0,
        msoAnimateTextByAllLevels : 1,
        msoAnimateTextByFirstLevel : 2,
        msoAnimateTextBySecondLevel : 3,
        msoAnimateTextByThirdLevel : 4,
        msoAnimateTextByFourthLevel : 5,
        msoAnimateTextByFifthLevel : 6,
        msoAnimateChartAllAtOnce : 7,
        msoAnimateChartByCategory : 8,
        msoAnimateChartByCategoryElements : 9,
        msoAnimateChartBySeries : 10,
        msoAnimateChartBySeriesElements : 11,
        msoAnimateDiagramAllAtOnce : 12,
        msoAnimateDiagramDepthByNode : 13,
        msoAnimateDiagramDepthByBranch : 14,
        msoAnimateDiagramBreadthByNode : 15,
        msoAnimateDiagramBreadthByLevel : 16,
        msoAnimateDiagramClockwise : 17,
        msoAnimateDiagramClockwiseIn : 18,
        msoAnimateDiagramClockwiseOut : 19,
        msoAnimateDiagramCounterClockwise : 20,
        msoAnimateDiagramCounterClockwiseIn : 21,
        msoAnimateDiagramCounterClockwiseOut : 22,
        msoAnimateDiagramInByRing : 23,
        msoAnimateDiagramOutByRing : 24,
        msoAnimateDiagramUp : 25,
        msoAnimateDiagramDown : 26
    }

     
    var MsoAnimTriggerType = {
        msoAnimTriggerMixed : -1,
        msoAnimTriggerNone : 0,
        msoAnimTriggerOnPageClick : 1,
        msoAnimTriggerWithPrevious : 2,
        msoAnimTriggerAfterPrevious : 3,
        msoAnimTriggerOnShapeClick : 4,
        msoAnimTriggerOnMediaBookmark : 5
    }

     
    var MsoAnimAfterEffect = {
        msoAnimAfterEffectMixed : -1,
        msoAnimAfterEffectNone : 0,
        msoAnimAfterEffectDim : 1,
        msoAnimAfterEffectHide : 2,
        msoAnimAfterEffectHideOnNextClick : 3
    }

     
    var MsoAnimTextUnitEffect = {
        msoAnimTextUnitEffectMixed : -1,
        msoAnimTextUnitEffectByParagraph : 0,
        msoAnimTextUnitEffectByCharacter : 1,
        msoAnimTextUnitEffectByWord : 2
    }

     
    var MsoAnimEffectRestart = {
        msoAnimEffectRestartAlways : 1,
        msoAnimEffectRestartWhenOff : 2,
        msoAnimEffectRestartNever : 3
    }

     
    var MsoAnimEffectAfter = {
        msoAnimEffectAfterFreeze : 1,
        msoAnimEffectAfterRemove : 2,
        msoAnimEffectAfterHold : 3,
        msoAnimEffectAfterTransition : 4
    }

     
    var MsoAnimDirection = {
        msoAnimDirectionNone : 0,
        msoAnimDirectionUp : 1,
        msoAnimDirectionRight : 2,
        msoAnimDirectionDown : 3,
        msoAnimDirectionLeft : 4,
        msoAnimDirectionOrdinalMask : 5,
        msoAnimDirectionUpLeft : 6,
        msoAnimDirectionUpRight : 7,
        msoAnimDirectionDownRight : 8,
        msoAnimDirectionDownLeft : 9,
        msoAnimDirectionTop : 10,
        msoAnimDirectionBottom : 11,
        msoAnimDirectionTopLeft : 12,
        msoAnimDirectionTopRight : 13,
        msoAnimDirectionBottomRight : 14,
        msoAnimDirectionBottomLeft : 15,
        msoAnimDirectionHorizontal : 16,
        msoAnimDirectionVertical : 17,
        msoAnimDirectionAcross : 18,
        msoAnimDirectionIn : 19,
        msoAnimDirectionOut : 20,
        msoAnimDirectionClockwise : 21,
        msoAnimDirectionCounterclockwise : 22,
        msoAnimDirectionHorizontalIn : 23,
        msoAnimDirectionHorizontalOut : 24,
        msoAnimDirectionVerticalIn : 25,
        msoAnimDirectionVerticalOut : 26,
        msoAnimDirectionSlightly : 27,
        msoAnimDirectionCenter : 28,
        msoAnimDirectionInSlightly : 29,
        msoAnimDirectionInCenter : 30,
        msoAnimDirectionInBottom : 31,
        msoAnimDirectionOutSlightly : 32,
        msoAnimDirectionOutCenter : 33,
        msoAnimDirectionOutBottom : 34,
        msoAnimDirectionFontBold : 35,
        msoAnimDirectionFontItalic : 36,
        msoAnimDirectionFontUnderline : 37,
        msoAnimDirectionFontStrikethrough : 38,
        msoAnimDirectionFontShadow : 39,
        msoAnimDirectionFontAllCaps : 40,
        msoAnimDirectionInstant : 41,
        msoAnimDirectionGradual : 42,
        msoAnimDirectionCycleClockwise : 43,
        msoAnimDirectionCycleCounterclockwise : 44
    }

     
    var MsoAnimType = {
        msoAnimTypeMixed : -2,
        msoAnimTypeNone : 0,
        msoAnimTypeMotion : 1,
        msoAnimTypeColor : 2,
        msoAnimTypeScale : 3,
        msoAnimTypeRotation : 4,
        msoAnimTypeProperty : 5,
        msoAnimTypeCommand : 6,
        msoAnimTypeFilter : 7,
        msoAnimTypeSet : 8
    }

     
    var MsoAnimAdditive = {
        msoAnimAdditiveAddBase : 1,
        msoAnimAdditiveAddSum : 2
    }

     
    var MsoAnimAccumulate = {
        msoAnimAccumulateNone : 1,
        msoAnimAccumulateAlways : 2
    }

     
    var MsoAnimProperty = {
        msoAnimNone : 0,
        msoAnimX : 1,
        msoAnimY : 2,
        msoAnimWidth : 3,
        msoAnimHeight : 4,
        msoAnimOpacity : 5,
        msoAnimRotation : 6,
        msoAnimColor : 7,
        msoAnimVisibility : 8,
        msoAnimTextFontBold : 100,
        msoAnimTextFontColor : 101,
        msoAnimTextFontEmboss : 102,
        msoAnimTextFontItalic : 103,
        msoAnimTextFontName : 104,
        msoAnimTextFontShadow : 105,
        msoAnimTextFontSize : 106,
        msoAnimTextFontSubscript : 107,
        msoAnimTextFontSuperscript : 108,
        msoAnimTextFontUnderline : 109,
        msoAnimTextFontStrikeThrough : 110,
        msoAnimTextBulletCharacter : 111,
        msoAnimTextBulletFontName : 112,
        msoAnimTextBulletNumber : 113,
        msoAnimTextBulletColor : 114,
        msoAnimTextBulletRelativeSize : 115,
        msoAnimTextBulletStyle : 116,
        msoAnimTextBulletType : 117,
        msoAnimShapePictureContrast : 1000,
        msoAnimShapePictureBrightness : 1001,
        msoAnimShapePictureGamma : 1002,
        msoAnimShapePictureGrayscale : 1003,
        msoAnimShapeFillOn : 1004,
        msoAnimShapeFillColor : 1005,
        msoAnimShapeFillOpacity : 1006,
        msoAnimShapeFillBackColor : 1007,
        msoAnimShapeLineOn : 1008,
        msoAnimShapeLineColor : 1009,
        msoAnimShapeShadowOn : 1010,
        msoAnimShapeShadowType : 1011,
        msoAnimShapeShadowColor : 1012,
        msoAnimShapeShadowOpacity : 1013,
        msoAnimShapeShadowOffsetX : 1014,
        msoAnimShapeShadowOffsetY : 1015
    }

     
    var PpAlertLevel = {
        ppAlertsNone : 1,
        ppAlertsAll : 2
    }

     
    var PpRevisionInfo = {
        ppRevisionInfoNone : 0,
        ppRevisionInfoBaseline : 1,
        ppRevisionInfoMerged : 2
    }

     
    var MsoAnimCommandType = {
        msoAnimCommandTypeEvent : 0,
        msoAnimCommandTypeCall : 1,
        msoAnimCommandTypeVerb : 2
    }

     
    var MsoAnimFilterEffectType = {
        msoAnimFilterEffectTypeNone : 0,
        msoAnimFilterEffectTypeBarn : 1,
        msoAnimFilterEffectTypeBlinds : 2,
        msoAnimFilterEffectTypeBox : 3,
        msoAnimFilterEffectTypeCheckerboard : 4,
        msoAnimFilterEffectTypeCircle : 5,
        msoAnimFilterEffectTypeDiamond : 6,
        msoAnimFilterEffectTypeDissolve : 7,
        msoAnimFilterEffectTypeFade : 8,
        msoAnimFilterEffectTypeImage : 9,
        msoAnimFilterEffectTypePixelate : 10,
        msoAnimFilterEffectTypePlus : 11,
        msoAnimFilterEffectTypeRandomBar : 12,
        msoAnimFilterEffectTypeSlide : 13,
        msoAnimFilterEffectTypeStretch : 14,
        msoAnimFilterEffectTypeStrips : 15,
        msoAnimFilterEffectTypeWedge : 16,
        msoAnimFilterEffectTypeWheel : 17,
        msoAnimFilterEffectTypeWipe : 18
    }

     
    var MsoAnimFilterEffectSubtype = {
        msoAnimFilterEffectSubtypeNone : 0,
        msoAnimFilterEffectSubtypeInVertical : 1,
        msoAnimFilterEffectSubtypeOutVertical : 2,
        msoAnimFilterEffectSubtypeInHorizontal : 3,
        msoAnimFilterEffectSubtypeOutHorizontal : 4,
        msoAnimFilterEffectSubtypeHorizontal : 5,
        msoAnimFilterEffectSubtypeVertical : 6,
        msoAnimFilterEffectSubtypeIn : 7,
        msoAnimFilterEffectSubtypeOut : 8,
        msoAnimFilterEffectSubtypeAcross : 9,
        msoAnimFilterEffectSubtypeFromLeft : 10,
        msoAnimFilterEffectSubtypeFromRight : 11,
        msoAnimFilterEffectSubtypeFromTop : 12,
        msoAnimFilterEffectSubtypeFromBottom : 13,
        msoAnimFilterEffectSubtypeDownLeft : 14,
        msoAnimFilterEffectSubtypeUpLeft : 15,
        msoAnimFilterEffectSubtypeDownRight : 16,
        msoAnimFilterEffectSubtypeUpRight : 17,
        msoAnimFilterEffectSubtypeSpokes1 : 18,
        msoAnimFilterEffectSubtypeSpokes2 : 19,
        msoAnimFilterEffectSubtypeSpokes3 : 20,
        msoAnimFilterEffectSubtypeSpokes4 : 21,
        msoAnimFilterEffectSubtypeSpokes8 : 22,
        msoAnimFilterEffectSubtypeLeft : 23,
        msoAnimFilterEffectSubtypeRight : 24,
        msoAnimFilterEffectSubtypeDown : 25,
        msoAnimFilterEffectSubtypeUp : 26
    }

     
    var PpRemoveDocInfoType = {
        ppRDIComments : 1,
        ppRDIRemovePersonalInformation : 4,
        ppRDIDocumentProperties : 8,
        ppRDIDocumentWorkspace : 10,
        ppRDIInkAnnotations : 11,
        ppRDIPublishPath : 13,
        ppRDIDocumentServerProperties : 14,
        ppRDIDocumentManagementPolicy : 15,
        ppRDIContentType : 16,
        ppRDISlideUpdateInformation : 17,
        ppRDIAll : 99
    }

     
    var PpCheckInVersionType = {
        ppCheckInMinorVersion : 0,
        ppCheckInMajorVersion : 1,
        ppCheckInOverwriteVersion : 2
    }

     
    var MsoClickState = {
        msoClickStateAfterAllAnimations : -2,
        msoClickStateBeforeAutomaticAnimations : -1
    }

     
    var PpFixedFormatType = {
        ppFixedFormatTypeXPS : 1,
        ppFixedFormatTypePDF : 2
    }

     
    var PpFixedFormatIntent = {
        ppFixedFormatIntentScreen : 1,
        ppFixedFormatIntentPrint : 2
    }

     
    var XlBackground = {
        xlBackgroundAutomatic : -4105,
        xlBackgroundOpaque : 3,
        xlBackgroundTransparent : 2
    }

     
    var XlChartGallery = {
        xlBuiltIn : 21,
        xlUserDefined : 22,
        xlAnyGallery : 23
    }

     
    var XlChartPicturePlacement = {
        xlSides : 1,
        xlEnd : 2,
        xlEndSides : 3,
        xlFront : 4,
        xlFrontSides : 5,
        xlFrontEnd : 6,
        xlAllFaces : 7
    }

     
    var XlDataLabelSeparator = {
        xlDataLabelSeparatorDefault : 1
    }

     
    var XlPattern = {
        xlPatternAutomatic : -4105,
        xlPatternChecker : 9,
        xlPatternCrissCross : 16,
        xlPatternDown : -4121,
        xlPatternGray16 : 17,
        xlPatternGray25 : -4124,
        xlPatternGray50 : -4125,
        xlPatternGray75 : -4126,
        xlPatternGray8 : 18,
        xlPatternGrid : 15,
        xlPatternHorizontal : -4128,
        xlPatternLightDown : 13,
        xlPatternLightHorizontal : 11,
        xlPatternLightUp : 14,
        xlPatternLightVertical : 12,
        xlPatternNone : -4142,
        xlPatternSemiGray75 : 10,
        xlPatternSolid : 1,
        xlPatternUp : -4162,
        xlPatternVertical : -4166,
        xlPatternLinearGradient : 4000,
        xlPatternRectangularGradient : 4001
    }

     
    var XlPictureAppearance = {
        xlPrinter : 2,
        xlScreen : 1
    }

     
    var XlCopyPictureFormat = {
        xlBitmap : 2,
        xlPicture : -4147
    }

     
    var XlRgbColor = {
        rgbAliceBlue : 16775408,
        rgbAntiqueWhite : 14150650,
        rgbAqua : 16776960,
        rgbAquamarine : 13959039,
        rgbAzure : 16777200,
        rgbBeige : 14480885,
        rgbBisque : 12903679,
        rgbBlack : 0,
        rgbBlanchedAlmond : 13495295,
        rgbBlue : 16711680,
        rgbBlueViolet : 14822282,
        rgbBrown : 2763429,
        rgbBurlyWood : 8894686,
        rgbCadetBlue : 10526303,
        rgbChartreuse : 65407,
        rgbCoral : 5275647,
        rgbCornflowerBlue : 15570276,
        rgbCornsilk : 14481663,
        rgbCrimson : 3937500,
        rgbDarkBlue : 9109504,
        rgbDarkCyan : 9145088,
        rgbDarkGoldenrod : 755384,
        rgbDarkGreen : 25600,
        rgbDarkGray : 11119017,
        rgbDarkGrey : 11119017,
        rgbDarkKhaki : 7059389,
        rgbDarkMagenta : 9109643,
        rgbDarkOliveGreen : 3107669,
        rgbDarkOrange : 36095,
        rgbDarkOrchid : 13382297,
        rgbDarkRed : 139,
        rgbDarkSalmon : 8034025,
        rgbDarkSeaGreen : 9419919,
        rgbDarkSlateBlue : 9125192,
        rgbDarkSlateGray : 5197615,
        rgbDarkSlateGrey : 5197615,
        rgbDarkTurquoise : 13749760,
        rgbDarkViolet : 13828244,
        rgbDeepPink : 9639167,
        rgbDeepSkyBlue : 16760576,
        rgbDimGray : 6908265,
        rgbDimGrey : 6908265,
        rgbDodgerBlue : 16748574,
        rgbFireBrick : 2237106,
        rgbFloralWhite : 15792895,
        rgbForestGreen : 2263842,
        rgbFuchsia : 16711935,
        rgbGainsboro : 14474460,
        rgbGhostWhite : 16775416,
        rgbGold : 55295,
        rgbGoldenrod : 2139610,
        rgbGray : 8421504,
        rgbGreen : 32768,
        rgbGrey : 8421504,
        rgbGreenYellow : 3145645,
        rgbHoneydew : 15794160,
        rgbHotPink : 11823615,
        rgbIndianRed : 6053069,
        rgbIndigo : 8519755,
        rgbIvory : 15794175,
        rgbKhaki : 9234160,
        rgbLavender : 16443110,
        rgbLavenderBlush : 16118015,
        rgbLawnGreen : 64636,
        rgbLemonChiffon : 13499135,
        rgbLightBlue : 15128749,
        rgbLightCoral : 8421616,
        rgbLightCyan : 9145088,
        rgbLightGoldenrodYellow : 13826810,
        rgbLightGray : 13882323,
        rgbLightGreen : 9498256,
        rgbLightGrey : 13882323,
        rgbLightPink : 12695295,
        rgbLightSalmon : 8036607,
        rgbLightSeaGreen : 11186720,
        rgbLightSkyBlue : 16436871,
        rgbLightSlateGray : 10061943,
        rgbLightSlateGrey : 10061943,
        rgbLightSteelBlue : 14599344,
        rgbLightYellow : 14745599,
        rgbLime : 65280,
        rgbLimeGreen : 3329330,
        rgbLinen : 15134970,
        rgbMaroon : 128,
        rgbMediumAquamarine : 11206502,
        rgbMediumBlue : 13434880,
        rgbMediumOrchid : 13850042,
        rgbMediumPurple : 14381203,
        rgbMediumSeaGreen : 7451452,
        rgbMediumSlateBlue : 15624315,
        rgbMediumSpringGreen : 10156544,
        rgbMediumTurquoise : 13422920,
        rgbMediumVioletRed : 8721863,
        rgbMidnightBlue : 7346457,
        rgbMintCream : 16449525,
        rgbMistyRose : 14804223,
        rgbMoccasin : 11920639,
        rgbNavajoWhite : 11394815,
        rgbNavy : 8388608,
        rgbNavyBlue : 8388608,
        rgbOldLace : 15136253,
        rgbOlive : 32896,
        rgbOliveDrab : 2330219,
        rgbOrange : 42495,
        rgbOrangeRed : 17919,
        rgbOrchid : 14053594,
        rgbPaleGoldenrod : 7071982,
        rgbPaleGreen : 10025880,
        rgbPaleTurquoise : 15658671,
        rgbPaleVioletRed : 9662683,
        rgbPapayaWhip : 14020607,
        rgbPeachPuff : 12180223,
        rgbPeru : 4163021,
        rgbPink : 13353215,
        rgbPlum : 14524637,
        rgbPowderBlue : 15130800,
        rgbPurple : 8388736,
        rgbRed : 255,
        rgbRosyBrown : 9408444,
        rgbRoyalBlue : 14772545,
        rgbSalmon : 7504122,
        rgbSandyBrown : 6333684,
        rgbSeaGreen : 5737262,
        rgbSeashell : 15660543,
        rgbSienna : 2970272,
        rgbSilver : 12632256,
        rgbSkyBlue : 15453831,
        rgbSlateBlue : 13458026,
        rgbSlateGray : 9470064,
        rgbSlateGrey : 9470064,
        rgbSnow : 16448255,
        rgbSpringGreen : 8388352,
        rgbSteelBlue : 11829830,
        rgbTan : 9221330,
        rgbTeal : 8421376,
        rgbThistle : 14204888,
        rgbTomato : 4678655,
        rgbTurquoise : 13688896,
        rgbYellow : 65535,
        rgbYellowGreen : 3329434,
        rgbViolet : 15631086,
        rgbWheat : 11788021,
        rgbWhite : 16777215,
        rgbWhiteSmoke : 16119285
    }

     
    var XlLineStyle = {
        xlContinuous : 1,
        xlDash : -4115,
        xlDashDot : 4,
        xlDashDotDot : 5,
        xlDot : -4118,
        xlDouble : -4119,
        xlSlantDashDot : 13,
        xlLineStyleNone : -4142
    }

     
    var XlAxisCrosses = {
        xlAxisCrossesAutomatic : -4105,
        xlAxisCrossesCustom : -4114,
        xlAxisCrossesMaximum : 2,
        xlAxisCrossesMinimum : 4
    }

     
    var XlAxisGroup = {
        xlPrimary : 1,
        xlSecondary : 2
    }

     
    var XlAxisType = {
        xlCategory : 1,
        xlSeriesAxis : 3,
        xlValue : 2
    }

     
    var XlBarShape = {
        xlBox : 0,
        xlPyramidToPoint : 1,
        xlPyramidToMax : 2,
        xlCylinder : 3,
        xlConeToPoint : 4,
        xlConeToMax : 5
    }

     
    var XlBorderWeight = {
        xlHairline : 1,
        xlMedium : -4138,
        xlThick : 4,
        xlThin : 2
    }

     
    var XlCategoryType = {
        xlCategoryScale : 2,
        xlTimeScale : 3,
        xlAutomaticScale : -4105
    }

     
    var XlChartElementPosition = {
        xlChartElementPositionAutomatic : -4105,
        xlChartElementPositionCustom : -4114
    }

     
    var XlChartItem = {
        xlDataLabel : 0,
        xlChartArea : 2,
        xlSeries : 3,
        xlChartTitle : 4,
        xlWalls : 5,
        xlCorners : 6,
        xlDataTable : 7,
        xlTrendline : 8,
        xlErrorBars : 9,
        xlXErrorBars : 10,
        xlYErrorBars : 11,
        xlLegendEntry : 12,
        xlLegendKey : 13,
        xlShape : 14,
        xlMajorGridlines : 15,
        xlMinorGridlines : 16,
        xlAxisTitle : 17,
        xlUpBars : 18,
        xlPlotArea : 19,
        xlDownBars : 20,
        xlAxis : 21,
        xlSeriesLines : 22,
        xlFloor : 23,
        xlLegend : 24,
        xlHiLoLines : 25,
        xlDropLines : 26,
        xlRadarAxisLabels : 27,
        xlNothing : 28,
        xlLeaderLines : 29,
        xlDisplayUnitLabel : 30,
        xlPivotChartFieldButton : 31,
        xlPivotChartDropZone : 32
    }

     
    var XlOrientation = {
        xlDownward : -4170,
        xlHorizontal : -4128,
        xlUpward : -4171,
        xlVertical : -4166
    }

     
    var XlChartPictureType = {
        xlStackScale : 3,
        xlStack : 2,
        xlStretch : 1
    }

     
    var XlChartSplitType = {
        xlSplitByPosition : 1,
        xlSplitByPercentValue : 3,
        xlSplitByCustomSplit : 4,
        xlSplitByValue : 2
    }

     
    var XlColorIndex = {
        xlColorIndexAutomatic : -4105,
        xlColorIndexNone : -4142
    }

     
    var XlConstants = {
        xlAutomatic : -4105,
        xlCombination : -4111,
        xlCustom : -4114,
        xlBar : 2,
        xlColumn : 3,
        xl3DBar : -4099,
        xl3DSurface : -4103,
        xlDefaultAutoFormat : -1,
        xlNone : -4142,
        xlAbove : 0,
        xlBelow : 1,
        xlBoth : 1,
        xlBottom : -4107,
        xlCenter : -4108,
        xlChecker : 9,
        xlCircle : 8,
        xlCorner : 2,
        xlCrissCross : 16,
        xlCross : 4,
        xlDiamond : 2,
        xlDistributed : -4117,
        xlFill : 5,
        xlFixedValue : 1,
        xlGeneral : 1,
        xlGray16 : 17,
        xlGray25 : -4124,
        xlGray50 : -4125,
        xlGray75 : -4126,
        xlGray8 : 18,
        xlGrid : 15,
        xlHigh : -4127,
        xlInside : 2,
        xlJustify : -4130,
        xlLeft : -4131,
        xlLightDown : 13,
        xlLightHorizontal : 11,
        xlLightUp : 14,
        xlLightVertical : 12,
        xlLow : -4134,
        xlMaximum : 2,
        xlMinimum : 4,
        xlMinusValues : 3,
        xlNextToAxis : 4,
        xlOpaque : 3,
        xlOutside : 3,
        xlPercent : 2,
        xlPlus : 9,
        xlPlusValues : 2,
        xlRight : -4152,
        xlScale : 3,
        xlSemiGray75 : 10,
        xlShowLabel : 4,
        xlShowLabelAndPercent : 5,
        xlShowPercent : 3,
        xlShowValue : 2,
        xlSingle : 2,
        xlSolid : 1,
        xlSquare : 1,
        xlStar : 5,
        xlStError : 4,
        xlTop : -4160,
        xlTransparent : 2,
        xlTriangle : 3
    }

     
    var XlDataLabelPosition = {
        xlLabelPositionCenter : -4108,
        xlLabelPositionAbove : 0,
        xlLabelPositionBelow : 1,
        xlLabelPositionLeft : -4131,
        xlLabelPositionRight : -4152,
        xlLabelPositionOutsideEnd : 2,
        xlLabelPositionInsideEnd : 3,
        xlLabelPositionInsideBase : 4,
        xlLabelPositionBestFit : 5,
        xlLabelPositionMixed : 6,
        xlLabelPositionCustom : 7
    }

     
    var XlDataLabelsType = {
        xlDataLabelsShowNone : -4142,
        xlDataLabelsShowValue : 2,
        xlDataLabelsShowPercent : 3,
        xlDataLabelsShowLabel : 4,
        xlDataLabelsShowLabelAndPercent : 5,
        xlDataLabelsShowBubbleSizes : 6
    }

     
    var XlDisplayBlanksAs = {
        xlInterpolated : 3,
        xlNotPlotted : 1,
        xlZero : 2
    }

     
    var XlDisplayUnit = {
        xlHundreds : -2,
        xlThousands : -3,
        xlTenThousands : -4,
        xlHundredThousands : -5,
        xlMillions : -6,
        xlTenMillions : -7,
        xlHundredMillions : -8,
        xlThousandMillions : -9,
        xlMillionMillions : -10
    }

     
    var XlEndStyleCap = {
        xlCap : 1,
        xlNoCap : 2
    }

     
    var XlErrorBarDirection = {
        xlChartX : -4168,
        xlChartY : 1
    }

     
    var XlErrorBarInclude = {
        xlErrorBarIncludeBoth : 1,
        xlErrorBarIncludeMinusValues : 3,
        xlErrorBarIncludeNone : -4142,
        xlErrorBarIncludePlusValues : 2
    }

     
    var XlErrorBarType = {
        xlErrorBarTypeCustom : -4114,
        xlErrorBarTypeFixedValue : 1,
        xlErrorBarTypePercent : 2,
        xlErrorBarTypeStDev : -4155,
        xlErrorBarTypeStError : 4
    }

     
    var XlHAlign = {
        xlHAlignCenter : -4108,
        xlHAlignCenterAcrossSelection : 7,
        xlHAlignDistributed : -4117,
        xlHAlignFill : 5,
        xlHAlignGeneral : 1,
        xlHAlignJustify : -4130,
        xlHAlignLeft : -4131,
        xlHAlignRight : -4152
    }

     
    var XlLegendPosition = {
        xlLegendPositionBottom : -4107,
        xlLegendPositionCorner : 2,
        xlLegendPositionLeft : -4131,
        xlLegendPositionRight : -4152,
        xlLegendPositionTop : -4160,
        xlLegendPositionCustom : -4161
    }

     
    var XlMarkerStyle = {
        xlMarkerStyleAutomatic : -4105,
        xlMarkerStyleCircle : 8,
        xlMarkerStyleDash : -4115,
        xlMarkerStyleDiamond : 2,
        xlMarkerStyleDot : -4118,
        xlMarkerStyleNone : -4142,
        xlMarkerStylePicture : -4147,
        xlMarkerStylePlus : 9,
        xlMarkerStyleSquare : 1,
        xlMarkerStyleStar : 5,
        xlMarkerStyleTriangle : 3,
        xlMarkerStyleX : -4168
    }

     
    var XlPivotFieldOrientation = {
        xlColumnField : 2,
        xlDataField : 4,
        xlHidden : 0,
        xlPageField : 3,
        xlRowField : 1
    }

     
    var XlReadingOrder = {
        xlContext : -5002,
        xlLTR : -5003,
        xlRTL : -5004
    }

     
    var XlRowCol = {
        xlColumns : 2,
        xlRows : 1
    }

     
    var XlScaleType = {
        xlScaleLinear : -4132,
        xlScaleLogarithmic : -4133
    }

     
    var XlSizeRepresents = {
        xlSizeIsWidth : 2,
        xlSizeIsArea : 1
    }

     
    var XlTickLabelOrientation = {
        xlTickLabelOrientationAutomatic : -4105,
        xlTickLabelOrientationDownward : -4170,
        xlTickLabelOrientationHorizontal : -4128,
        xlTickLabelOrientationUpward : -4171,
        xlTickLabelOrientationVertical : -4166
    }

     
    var XlTickLabelPosition = {
        xlTickLabelPositionHigh : -4127,
        xlTickLabelPositionLow : -4134,
        xlTickLabelPositionNextToAxis : 4,
        xlTickLabelPositionNone : -4142
    }

     
    var XlTickMark = {
        xlTickMarkCross : 4,
        xlTickMarkInside : 2,
        xlTickMarkNone : -4142,
        xlTickMarkOutside : 3
    }

     
    var XlTimeUnit = {
        xlDays : 0,
        xlMonths : 1,
        xlYears : 2
    }

     
    var XlTrendlineType = {
        xlExponential : 5,
        xlLinear : -4132,
        xlLogarithmic : -4133,
        xlMovingAvg : 6,
        xlPolynomial : 3,
        xlPower : 4
    }

     
    var XlUnderlineStyle = {
        xlUnderlineStyleDouble : -4119,
        xlUnderlineStyleDoubleAccounting : 5,
        xlUnderlineStyleNone : -4142,
        xlUnderlineStyleSingle : 2,
        xlUnderlineStyleSingleAccounting : 4
    }

     
    var XlVAlign = {
        xlVAlignBottom : -4107,
        xlVAlignCenter : -4108,
        xlVAlignDistributed : -4117,
        xlVAlignJustify : -4130,
        xlVAlignTop : -4160
    }

     
    var PpResampleMediaProfile = {
        ppResampleMediaProfileCustom : 1,
        ppResampleMediaProfileSmall : 2,
        ppResampleMediaProfileSmaller : 3,
        ppResampleMediaProfileSmallest : 4
    }

     
    var PpMediaTaskStatus = {
        ppMediaTaskStatusNone : 0,
        ppMediaTaskStatusInProgress : 1,
        ppMediaTaskStatusQueued : 2,
        ppMediaTaskStatusDone : 3,
        ppMediaTaskStatusFailed : 4
    }

     
    var PpPlayerState = {
        ppPlaying : 0,
        ppPaused : 1,
        ppStopped : 2,
        ppNotReady : 3
    }

     
    var XlPieSliceLocation = {
        xlHorizontalCoordinate : 1,
        xlVerticalCoordinate : 2
    }

     
    var XlPieSliceIndex = {
        xlOuterCounterClockwisePoint : 1,
        xlOuterCenterPoint : 2,
        xlOuterClockwisePoint : 3,
        xlMidClockwiseRadiusPoint : 4,
        xlCenterPoint : 5,
        xlMidCounterClockwiseRadiusPoint : 6,
        xlInnerClockwisePoint : 7,
        xlInnerCenterPoint : 8,
        xlInnerCounterClockwisePoint : 9
    }

     
    var PpProtectedViewCloseReason = {
        ppProtectedViewCloseNormal : 0,
        ppProtectedViewCloseEdit : 1,
        ppProtectedViewCloseForced : 2
    }

     
    var XlCategoryLabelLevel = {
        xlCategoryLabelLevelNone : -3,
        xlCategoryLabelLevelCustom : -2,
        xlCategoryLabelLevelAll : -1
    }

     
    var XlSeriesNameLevel = {
        xlSeriesNameLevelNone : -3,
        xlSeriesNameLevelCustom : -2,
        xlSeriesNameLevelAll : -1
    }

     
    var PpGuideOrientation = {
        ppHorizontalGuide : 1,
        ppVerticalGuide : 2
    }