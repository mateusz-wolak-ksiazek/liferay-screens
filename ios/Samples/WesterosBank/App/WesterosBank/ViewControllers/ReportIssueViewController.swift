/**
* Copyright (c) 2000-present Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
import UIKit
import LiferayScreens

class ReportIssueViewController: CardViewController, DDLFormScreenletDelegate {

	@IBOutlet weak var screenlet: DDLFormScreenlet?
	@IBOutlet weak var saveButton: UIButton!

	var issueRecord: DDLRecord?
	var editable: Bool = true

	override init(card: CardView, nibName: String) {
		super.init(card: card, nibName: nibName)
	}

	convenience init(card: CardView) {
		self.init(card: card, nibName: "ReportIssueViewController")
	}

	required init?(coder aDecoder: NSCoder) {
		super.init(coder: aDecoder)
	}

	override func viewDidLoad() {
		screenlet?.delegate = self

	}

	override func cardWillAppear() {
		screenlet?.editable = editable
		saveButton.isHidden = !editable

		if let recordValue = issueRecord {
			screenlet?.recordId = recordValue.recordId!
			screenlet?.loadRecord()
		}
		else {
			screenlet?.recordId = 0
			
			if (screenlet!.isFormLoaded) {
				screenlet?.clearForm()
			}
			else {
				screenlet?.loadForm()
			}
		}
	}

	func screenlet(_ screenlet: DDLFormScreenlet, onFormSubmitted record: DDLRecord) {
		onDone?()
	}

	@IBAction func saveButtonClick(_ sender: UIButton) {
		screenlet?.performAction(name: sender.restorationIdentifier!)
	}

}
