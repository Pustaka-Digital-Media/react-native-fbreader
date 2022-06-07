import FBReaderSDK

@objc(FBReader)
class FBReader: NSObject {

  @objc static func requiresMainQueueSetup() -> Bool {
      return false
  }

  @objc(tableOfContents:withResolver:withRejecter:)
  func tableOfContents(book: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    let widget = TextWidget()
    let screen = UIScreen.main
    let width = screen.bounds.width
    let height = screen.bounds.height
//    if #available(iOS 11.0, *) {
//      height = widget.safeAreaLayoutGuide.layoutFrame.size.height
//    }
    widget.frame = CGRect(x: 0, y: 0, width: width, height: height)

    let documentsDirectory = FBReaderView.getDocumentsDirectory()
    let bookPath = book.starts(with: documentsDirectory) ? String(book[documentsDirectory.endIndex..<book.endIndex]) : book

    if let book = BookLoader.book(from: bookPath, embedded: false) {
      widget.open(book: book, withProgressIndicator: nil) {
        success in
        let tocMap = self.toMap(node: widget.tableOfContentsTree, widget: widget)
        resolve(tocMap)
      }
    }
  }

  @objc(tableOfContents:withResolver:withRejecter:)
  func tableOfContents(page: Int) -> Void {
    textWidget = TextWidget()
    textWidget?.delegate = self
    textWidget?.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    addSubview(textWidget!)
    textWidget.goto(pageNo: page)
  }

  func toMap(node: ToCTree?, widget: TextWidget) -> [String: Any] {
    var map = [String: Any]()
    if let title = node?.title, let ref = node?.reference {
      map["title"] = title
      map["ref"] = ref
      map["page"] = widget.pageNo(for: ref + 1, in:.main)
    }

    if let children = node?.children {
      var lst = [Dictionary<String, Any>]()
      for child in children {
        lst.append(toMap(node: child, widget: widget))
      }
      if !lst.isEmpty {
        map["children"] = lst
      }
    }
    return map
  }
}
