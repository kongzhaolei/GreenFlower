package org.gradle.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class SwtDemo {
	public static void main(String[] args) {

		// 创建Display,对应操作系统的控件，使用完必须释放
		Display display = new Display();

		// 创建shell
		final Shell shell = new Shell(display);
		shell.setText("第一个shell窗口");

		// 指定容器的布局类型
		GridLayout gl = new GridLayout(2, false);
		gl.marginBottom = 20;
		gl.marginTop = 10;
		gl.marginLeft = 30;
		gl.marginRight = 30;
		gl.verticalSpacing = 20;
		gl.horizontalSpacing = 20;
		shell.setLayout(gl);

		// 创建容器里的控件，并指定摆放位置
		Label label1 = new Label(shell, SWT.NONE);
		label1.setText("姓名");
		// 第一个参数是操作系统资源 第二参数是字体样式 第三参数是字的高度（字号大小） 第4个参数是字显示样式
		Font labelFont = new Font(display, "微软雅黑", 20, SWT.NONE);
		Text text1 = new Text(shell, SWT.BORDER);
		text1.setFont(labelFont);
		text1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label label2 = new Label(shell, SWT.NONE);
		label2.setText("密码");
		Color foreColor = new Color(display, 255, 0, 0);
		label2.setForeground(foreColor);
		Text text2 = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		text2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label label3 = new Label(shell, SWT.NONE);
		label3.setText("下拉：");
		Combo combo = new Combo(shell, SWT.NONE);
		for (int i = 1; i <= 9; i++) {
			combo.add("选项" + i);
		}
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		/**
		 * 创建表格4列的表格
		 * 
		 * 1.创建表头 2.创建内容数据
		 */
		// 表格样式是多选 垂直 水平滚动条，显示边缘
		Table table = new Table(shell, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		table.setHeaderVisible(true);// 设置表格头可见
		table.setLinesVisible(true);// 设置表格线可见

		// 创建表格头
		for (int i = 1; i <= 4; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setText("第" + i + "列");
			tableColumn.setWidth(200);
		}
		// 创建几个数据
		TableItem item1 = new TableItem(table, SWT.NONE);
		item1.setText(new String[] { "111", "222", "3333", "444" });

		// 创建一个button
		Button btn = new Button(shell, SWT.NONE);
		btn.setText("提交");
		btn.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2,
				1));

		// 添加button的事件监听
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				MessageBox box = new MessageBox(shell, SWT.OK | SWT.CANCEL
						| SWT.ICON_QUESTION);
				box.setText("确认");
				box.setMessage("是否确认提交数据");
				box.open();
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}
}
