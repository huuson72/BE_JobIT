import os
from datetime import datetime
import json
from pathlib import Path
import time
import pyperclip
import keyboard
import tkinter as tk
from tkinter import scrolledtext
import win32gui
import win32con

class AutoChatLogger:
    def __init__(self):
        self.window = tk.Tk()
        self.window.title("Auto Chat Logger")
        self.window.geometry("800x600")
        
        # Tạo thư mục logs
        self.log_dir = "chat_logs"
        if not os.path.exists(self.log_dir):
            os.makedirs(self.log_dir)
        
        # Biến lưu trữ
        self.last_content = ""
        self.chat_history = []
        self.is_monitoring = True
        
        # Tạo file log cho ngày hiện tại
        self.current_date = datetime.now().strftime("%Y%m%d")
        self.log_file = os.path.join(self.log_dir, f"chat_log_{self.current_date}.txt")
        
        # Tạo giao diện
        self.create_widgets()
        
        # Bắt đầu theo dõi
        self.check_clipboard()
        
    def create_widgets(self):
        # Frame điều khiển
        control_frame = tk.Frame(self.window)
        control_frame.pack(fill=tk.X, padx=5, pady=5)
        
        self.monitor_button = tk.Button(control_frame, text="Tạm dừng", command=self.toggle_monitoring)
        self.monitor_button.pack(side=tk.LEFT, padx=5)
        
        self.status_label = tk.Label(control_frame, text="Đang theo dõi chat...", fg="green")
        self.status_label.pack(side=tk.LEFT, padx=5)
        
        # Khung hiển thị chat
        self.chat_display = scrolledtext.ScrolledText(self.window, wrap=tk.WORD, height=30)
        self.chat_display.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
    def toggle_monitoring(self):
        self.is_monitoring = not self.is_monitoring
        if self.is_monitoring:
            self.monitor_button.config(text="Tạm dừng")
            self.status_label.config(text="Đang theo dõi chat...", fg="green")
        else:
            self.monitor_button.config(text="Tiếp tục")
            self.status_label.config(text="Đã tạm dừng", fg="red")
    
    def check_clipboard(self):
        if self.is_monitoring:
            try:
                current_content = pyperclip.paste()
                
                # Kiểm tra nếu có nội dung mới và chứa từ khóa chat
                if current_content != self.last_content and len(current_content.strip()) > 0:
                    if "user:" in current_content.lower() or "assistant:" in current_content.lower():
                        self.last_content = current_content
                        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                        
                        # Lưu vào file
                        with open(self.log_file, "a", encoding="utf-8") as f:
                            f.write(f"\n[{timestamp}]\n")
                            f.write(current_content)
                            f.write("\n" + "="*50 + "\n")
                        
                        # Hiển thị trong khung chat
                        self.chat_display.insert(tk.END, f"\n[{timestamp}]\n")
                        self.chat_display.insert(tk.END, current_content + "\n")
                        self.chat_display.insert(tk.END, "="*50 + "\n")
                        self.chat_display.see(tk.END)
                        
                        # Cập nhật trạng thái
                        self.status_label.config(text=f"Đã lưu chat lúc: {timestamp}", fg="green")
                        
                        # Kiểm tra nếu sang ngày mới
                        current_date = datetime.now().strftime("%Y%m%d")
                        if current_date != self.current_date:
                            self.current_date = current_date
                            self.log_file = os.path.join(self.log_dir, f"chat_log_{self.current_date}.txt")
            
            except Exception as e:
                self.status_label.config(text=f"Lỗi: {str(e)}", fg="red")
        
        # Kiểm tra lại sau 1 giây
        self.window.after(1000, self.check_clipboard)
    
    def run(self):
        self.window.mainloop()

if __name__ == "__main__":
    app = AutoChatLogger()
    app.run() 