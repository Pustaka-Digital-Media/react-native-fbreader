import FBReaderSDK

class TextBookDataHolderImpl: TextBookDataHolder {
  let book: Book
  
  init(book: Book) {
    self.book = book
  }

  var position: TextPosition? = nil
  
  func save(position: TextPosition, progress: RationalNumber) {
    self.position = position
  }
  
  func highlightingStyle(_ id: Int) -> HighlightingStyle? {
    return HighlightingStyle(id: id, fgColor: nil, bgColor: 0xA00000, savedName: "Red", timestamp: 0)
  }
  
  private var _bookmarks = [Bookmark]()
  func bookmarks(for modelId: String?) -> [Bookmark] {
    return self._bookmarks.filter {$0.modelId == modelId}
  }

  func save(bookmark: Bookmark) -> Bookmark? {
    self._bookmarks.append(bookmark)
    return bookmark
  }

  var recentLocations = [TextLocation]()
  
  func save(recentLocations: [TextLocation]) {
    self.recentLocations = recentLocations
  }
}

@objc(FBReaderCoverViewManager)
class FBReaderCoverViewManager: RCTViewManager {

  override func view() -> (FBReaderCoverView) {
    return FBReaderCoverView()
  }
  
  @objc override static func requiresMainQueueSetup() -> Bool {
      return false
  }
}

class FBReaderCoverView: UIImageView {
  @objc var book: String = "" {
    didSet {
      let documentsDirectory = FBReaderView.getDocumentsDirectory()
      let bookPath = book.starts(with: documentsDirectory) ? String(book[documentsDirectory.endIndex..<book.endIndex]) : book

      let widget = TextWidget()
      if let book = BookLoader.book(from: bookPath, embedded: false) {
        widget.open(book: book, withProgressIndicator: nil) {
          success in
          if let coverData = book.coverData, let image = UIImage(data: coverData) {
            self.image = image
          }
        }
      }
    }
  }
}


@objc(FBReaderViewManager)
class FBReaderViewManager: RCTViewManager {

  override func view() -> (FBReaderView) {
    Options.loadDefaults()
    FBReaderView.setDefaults("day", forKey: "colorProfile")
    FBReaderView.setDefaults("wood", forKey: "night:textBgPattern")
    return FBReaderView()
  }
  
  @objc override static func requiresMainQueueSetup() -> Bool {
      return false
  }
}

class FBReaderView : UIView, TextWidgetDelegate {
  private var textWidget: TextWidget? = nil
  
  override func layoutSublayers(of layer: CALayer) {
    super.layoutSublayers(of: layer)
    if let widget = getTextWidget() {
      widget.frame = self.bounds
    }
  }

  // MARK:- Properties
  @objc var book: String = "" {
    didSet {
      let documentsDirectory = FBReaderView.getDocumentsDirectory()
      let bookPath = book.starts(with: documentsDirectory) ? String(book[documentsDirectory.endIndex..<book.endIndex]) : book

      if let widget = getTextWidget(), let book = BookLoader.book(from: bookPath, embedded: false) {
        widget.open(book: book, withProgressIndicator: nil) {
          success in
          widget.rebuildPaintInfo()
          widget.setNeedsDisplay()
        }
      }
    }
  }
  
  @objc var background = "" {
    didSet {
      if let widget = getTextWidget() {
        FBReaderView.setDefaults(background, forKey: "day:textBgPattern")
        FBReaderView.setDefaults(background, forKey: "night:textBgPattern")
        widget.forceUpdateTextParams()
        widget.rebuildPaintInfo()
        widget.setNeedsDisplay()
      }
    }
  }
  
  @objc var colorProfile = "" {
    didSet {
      if let widget = getTextWidget() {
        Options.colorProfileName = colorProfile
        widget.forceUpdateTextParams()
        widget.rebuildPaintInfo()
        widget.setNeedsDisplay()
      }
    }
  }
  
  @objc var fontSize = 10 {
    didSet {
      if let widget = getTextWidget() {
        Options.Reading.baseFontSize.value = fontSize
        widget.forceUpdateTextParams()
        widget.rebuildPaintInfo()
        widget.setNeedsDisplay()
      }
    }
  }

  @objc var searchInText = "" {
    didSet {
      if let widget = getTextWidget() {
        let _ = widget.search(pattern: searchInText, withProgressIndicator: nil)
      }
    }
  }
  
  @objc var tocReference = 0 {
    didSet {
      if let widget = getTextWidget() {
        let _ = widget.goto(position: TextFixedPosition(para: tocReference, element: 0, char: 0))
      }
    }
  }
  
  @objc var page = 0 {
    didSet {
      if let widget = getTextWidget() {
        let _ = widget.goto(pageNo: page)
      }
    }
  }
  
  // MARK:- Private methods
  private func getTextWidget() -> TextWidget? {
    if textWidget == nil {
      textWidget = TextWidget()
      textWidget?.delegate = self
      textWidget?.autoresizingMask = [.flexibleWidth, .flexibleHeight]
      addSubview(textWidget!)
    }
    return textWidget
  }
  
  static func getDocumentsDirectory() -> String {
    let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
    let documentsDirectory = paths[0]
    return documentsDirectory.path
  }
  
  static func setDefaults(_ value: Int, forKey key: String) {
    UserDefaults.standard.set(value, forKey: key)
  }
  
  static func setDefaults(_ value: String, forKey key: String) {
    UserDefaults.standard.set(value, forKey: key)
  }
  
  static func setDefaults(_ value: Bool, forKey key: String) {
    UserDefaults.standard.set(value, forKey: key)
  }

  // MARK:- TextWidgetDelegate
  func onTap(_ pt: CGPoint) {
    if let widget = getTextWidget() {
      if pt.x <= widget.frame.width / 2 {
        widget.turnBack(animated: true)
      } else if pt.x >= widget.frame.width / 2 {
        widget.turnForward(animated: true)
      }
    }
  }
  
  func onSelectionChanged(_ pt: CGPoint) {
  }
  
  func onSelectionCleared() {
  }

  func hidePopupElements() -> Bool {
    return false
  }
  
  func followFootnote(id: String, sourceRect: CGRect, openFootnoteHandler: @escaping (() -> Void)) {
  }
  
  func onFootnoteShown() {
  }
  
  func onCreated(bookmark: Bookmark, in rect: CGRect) {
  }
  
  func onTouched(bookmark: Bookmark, in rect: CGRect) {
  }
  
  func onLongTouchInVoiceOverMode() {
  }
  
  func openSelectionMenu(_ pt: CGPoint) {
  }
  
  func createDataHolder(book: Book) -> TextBookDataHolder {
    return TextBookDataHolderImpl(book: book)
  }
  
}
